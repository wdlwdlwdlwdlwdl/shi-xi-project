package com.aliyun.gts.gmall.center.trade.server.job;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookOrderRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.point.AcBookRecordParam;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantConfig;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantParam;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PointGrantRepository;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.promotion.common.constant.AcBookRecordReserveState;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeDirectionEnum;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.user.api.dto.common.OrderCompleteGrowthMessage;
import com.aliyun.gts.gmall.platform.user.api.dto.common.OrderCompleteWeekGrowthMessage;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 赠送积分保留JOB
 * TaskId: KALYK-36
 */
@Slf4j
@Component
public class PointGrantReserveXxlJob {

    private static final String  AC_BOOK_PREFIX = "ac-book-";

    private static final String KEY_CONFIG = "grantPointConfig";

    private static final String KEY_COUNT = "grantPointCount";

    @Value("${trade.order.complete.topic:}")
    private String completeTopic;
    @Value("${trade.order.complete.tag:}")
    private String completeTag;

    @Value("${trade.order.week.topic:}")
    private String weekTopic;
    @Value("${trade.order.week.tag:}")
    private String weekTag;

    @Autowired
    private OrderReadFacade orderReadFacade;

    @Resource
    MessageSendManager messageSendManager;

    @Autowired
    private CacheManager tradeCacheManager;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderExtraService orderExtraService;

    @Autowired
    private PointGrantRepository pointGrantRepository;

    @XxlJob(value = "pointGrantReserveJob")
    public ReturnT<String> pointGrantReserveJob(String jsonParam) {
        XxlJobHelper.log("pointGrantReserveJob process begin");
        //先获取配置
        PointGrantConfig config = pointGrantRepository.getGrantConfig();
        if (config == null) {
            XxlJobHelper.log("job finished for value of config [account.point.config] is null");
            return ReturnT.SUCCESS;
        }
        if (!config.getTradeGrantPoint()) {
            XxlJobHelper.log("job finished for value of config [account.point.config--tradeGrantPoint] is false");
            return ReturnT.SUCCESS;
        }
        if (config.getGrantPointOneYuan() == null ||  config.getGrantPointOneYuan() <= 0) {
            XxlJobHelper.log("job finished for value of config [account.point.config--grantPointValue] is null or zero or less than zero");
            return ReturnT.SUCCESS;
        }
        if (config.getGrantPointReserveDay() == null) {
            XxlJobHelper.log("job finished for value of config [account.point.config--grantPointReserveDay] is null");
            return ReturnT.SUCCESS;
        }
        // 查询配置日期过去5分钟生效的记录，防止一次处理不完
        AcBookRecordParam acBookRecordQuery = new AcBookRecordParam();
        acBookRecordQuery.setPage(new PageParam(1, 300));
        //查询保留中的
        acBookRecordQuery.setReserveState(AcBookRecordReserveState.RESERVED);
        //当前天数向前偏移得出保留结束日期
        acBookRecordQuery.setEffectTime(new Date());
        // 账户类型: 1 积分账户;默认是1
        acBookRecordQuery.setAccountType(1);
        acBookRecordQuery.setChangeType(ChangeTypeEnum.trade_grant.getCode());
        acBookRecordQuery.setChangeDirection(ChangeDirectionEnum.ADD.getCode());
        PageInfo<AcBookOrderRecordDO> acBookRecordDOPageInfo = pointGrantRepository.queryGrantOrderRecord(acBookRecordQuery);
        // 没查到， 结束
        if (acBookRecordDOPageInfo == null || CollectionUtils.isEmpty(acBookRecordDOPageInfo.getList())) {
            XxlJobHelper.log("There are no acBookRecord need to process");
            return ReturnT.SUCCESS;
        }
        // 查询到了，逐个处理
        XxlJobHelper.log("There are [{}] acBookRecords need to process", acBookRecordDOPageInfo.getList().size());
        ExecutorService executorService = new ThreadPoolExecutor(
            8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()
        );
        //批量处理，每个积分记录，单独线程，启动线程池处理
        acBookRecordDOPageInfo.getList().forEach(acBookRecordDO -> {
            executorService.submit(() -> {
                //以bookId为锁的key,防止同一条记录冲突，并且也防止同一条记录被多次处理
                String lockKey = AC_BOOK_PREFIX + acBookRecordDO.getId();
                DistributedLock lock = tradeCacheManager.getLock(lockKey);
                try {
                    if (lock != null && !lock.tryLock(1000L, 5000L)) {
                        XxlJobHelper.log("get Lock failed, key [{}]", lockKey);
                        return;
                    }
                    //查询记录，确定状态是1，最后更新为3
                    AcBookRecordDO exist = pointGrantRepository.queryGrantRecord(acBookRecordDO.getId());
                    if (exist == null || exist.getReserveState() != AcBookRecordReserveState.RESERVED) {
                        //不存在或者不为待处理，则返回
                        return;
                    }
                    // 积分记录修改为处理中
                    if (Boolean.FALSE.equals(updateProcessing(acBookRecordDO.getId()))) {
                        XxlJobHelper.log("update ac_book_record to processing failed, id [{}]", acBookRecordDO.getId());
                        return;
                    }
                    try {
                        MainOrder mainOrder = orderQueryAbility.getMainOrder(Long.valueOf(exist.getBizId()));
                        // 订单找不不到，直接结束
                        if (mainOrder == null || mainOrder.getPrimaryOrderId() == null) {
                            XxlJobHelper.log("update ac_book_record to reserve cancel for order does not exist, id [{}]", acBookRecordDO.getId());
                        }
                        // 订单状态异常
                        if (Boolean.FALSE.equals(checkOrderStatus(mainOrder.getPrimaryOrderStatus()))) {
                            XxlJobHelper.log("update ac_book_record to reserve cancel for order state is illegal, id [{}]", acBookRecordDO.getId());
                            updateCancel(acBookRecordDO.getId());
                            return;
                        }
                        long confirmRealAmt = calConfirmRealAmt(mainOrder);
                        //需要使用订单上边的当时的赠送配置
                        PointGrantConfig orderPointGrantConfig = queryFromOrder(Long.valueOf(exist.getBizId()));
                        if (orderPointGrantConfig == null) {
                            //如果没有，就用扫描现在配置
                            orderPointGrantConfig = config;
                            // 保存扩展结构
                            Map<String, String> extend = new HashMap<>();
                            extend.put(KEY_CONFIG, JSON.toJSONString(config));
                            Map<String, Map<String, String>> extendMap = new HashMap<>();
                            extendMap.put(KEY_CONFIG, extend);
                            OrderExtraSaveRpcReq save = new OrderExtraSaveRpcReq();
                            save.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                            save.setAddExtends(extendMap);
                            orderExtraService.saveOrderExtras(save);
                        }
                        // 根据确认订单实付金额, 计算赠积分个数
                        Long grantPointCount = calcGrantPoint(confirmRealAmt, orderPointGrantConfig);
                        if (grantPointCount == null || grantPointCount.longValue() <= 0) {
                            return; // 不赠积分
                        }
                        // 调用积分赠送
                        Date endDate = pointGrantRepository.calcInvalidDate(orderPointGrantConfig, new Date());
                        PointGrantParam pointGrantParam = new PointGrantParam();
                        pointGrantParam.setCustId(mainOrder.getCustomer().getCustId());
                        pointGrantParam.setMainOrderId(mainOrder.getPrimaryOrderId());
                        pointGrantParam.setCount(grantPointCount);
                        pointGrantParam.setInvalidDate(endDate);
                        pointGrantParam.setReserveState(AcBookRecordReserveState.RESERVE_FULFIL);
                        pointGrantParam.setAcBookRecordId(acBookRecordDO.getId());
                        pointGrantParam.setRemark(I18NMessageUtils.getMessage("normal.order") +  mainOrder.getPrimaryOrderId() + I18NMessageUtils.getMessage("points.award"));
                        //赠送完成后，要内部更新为RESERVE_FULFIL
                        pointGrantRepository.grantPoint(pointGrantParam);
                        // 记录赠送数量
                        Map<String, String> feature = new HashMap<>();
                        feature.put(KEY_COUNT, String.valueOf(grantPointCount));
                        OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
                        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                        req.setAddFeatures(feature);
                        orderExtraService.saveOrderExtras(req);

                        // 发成长值
                        OrderCompleteGrowthMessage orderCompleteGrowthMessage = new OrderCompleteGrowthMessage();
                        orderCompleteGrowthMessage.setTime(new Date());
                        orderCompleteGrowthMessage.setCustId(acBookRecordDO.getCustId());
                        orderCompleteGrowthMessage.setSellerId(mainOrder.getSeller().getSellerId());
                        orderCompleteGrowthMessage.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                        messageSendManager.sendMessage(orderCompleteGrowthMessage, completeTopic , completeTag);
                        // 发100 成长值
                        /**
                        OrderCompleteWeekGrowthMessage orderCompleteWeekGrowthMessage = new OrderCompleteWeekGrowthMessage();
                        orderCompleteGrowthMessage.setTime(new Date());
                        orderCompleteWeekGrowthMessage.setCustId(acBookRecordDO.getCustId());
                        messageSendManager.sendMessage(orderCompleteWeekGrowthMessage, weekTopic, weekTag);
                         **/
                    } finally {
                        //最终如果没有更新为RESERVE_FULFIL，则要重新更新为RESERVED等待下次处理
                        updateReseved(acBookRecordDO.getId());
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    if (lock != null) {
                        lock.unLock();
                    }
                }
            });
        });
        // 关闭线程池
        executorService.shutdown();
        while(!executorService.isTerminated()) {
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException e) {
            }
        }
        XxlJobHelper.log("pointGrantReserveJob process end");
        return ReturnT.SUCCESS;
    }

    /**
     * 计算赠送积分的实付金额、兼容预售多阶段场景、需要计算总实付金额
     *
     * @return
     */
    private static long calConfirmRealAmt(MainOrder mainOrder) {
        List<StepOrder> stepOrders = mainOrder.getStepOrders();
        if (CollectionUtils.isNotEmpty(stepOrders)) {
            long confirmRealAmt = 0;
            for (StepOrder stepOrder : stepOrders) {
                confirmRealAmt = confirmRealAmt + mainOrder.getPayInfo(stepOrder.getStepNo()).getPayPrice()
                    .getConfirmPrice().getConfirmRealAmt();
            }
            return confirmRealAmt;

        }
        //TODO 目前没有实际支付，所以如果实际支付为空，先取订单实际价格吧
        PayPrice payPrice = mainOrder.getCurrentPayInfo().getPayPrice();
        return payPrice.getConfirmPrice() == null ? payPrice.getOrderRealAmt(): payPrice.getConfirmPrice().getConfirmRealAmt();
    }

    private Long calcGrantPoint(Long realFee, PointGrantConfig g) {
        if (realFee == null || realFee <= 0L) {
            return null;
        }
        return realFee.longValue()  * g.getGrantPointOneYuan();
    }

    private PointGrantConfig queryFromOrder(Long primaryOrderId) {
        OrderExtendQueryRpcReq query = new OrderExtendQueryRpcReq();
        query.setPrimaryOrderId(primaryOrderId);
        query.setOrderId(primaryOrderId);
        query.setExtendType(KEY_CONFIG);
        query.setExtendKey(KEY_CONFIG);
        List<OrderExtendDTO> list = orderExtraService.queryOrderExtend(query);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        OrderExtendDTO ext = list.get(0);
        if (StringUtils.isBlank(ext.getExtendValue())) {
            return null;
        }
        return JSON.parseObject(ext.getExtendValue(), PointGrantConfig.class);
    }

    /**
     * 状态判读
     * @return
     */
    private Boolean checkOrderStatus(Integer orderStatus) {
        if (orderStatus == null) {
            return Boolean.FALSE;
        }
        Integer[] statusArray = new Integer[]{
            OrderStatusEnum.ORDER_BUYER_CANCEL.getCode(),
            OrderStatusEnum.ORDER_SELLER_CLOSE.getCode(),
            OrderStatusEnum.SYSTEM_CLOSE.getCode(),
            OrderStatusEnum.SELLER_CANCELING.getCode(),
            OrderStatusEnum.SELLER_AGREE_CANCEL.getCode(),
            OrderStatusEnum.REFUND_FULL_SUCCESS.getCode(),
            OrderStatusEnum.RETURNING_TO_MERCHANT.getCode(),
            OrderStatusEnum.CANCEL_REQUESTED.getCode()
        };
        return CollectionUtils.isEmpty(Arrays.stream(statusArray).filter(status -> orderStatus.equals(status)).collect(Collectors.toList()));
    }

    /**
     * 更新为进行中
     * @return
     */
    private Boolean updateProcessing(Long id) {
        return pointGrantRepository.updateGrantRecordReserveState(
            id,
            AcBookRecordReserveState.RESERVED,
            AcBookRecordReserveState.RESERVE_PROCESSING
        );
    }

    /**
     * 更新为取消
     * @return
     */
    private Boolean updateCancel(Long id) {
       return pointGrantRepository.updateGrantRecordReserveState(
           id,
           AcBookRecordReserveState.RESERVE_PROCESSING,
           AcBookRecordReserveState.RESERVE_CANCELED
        );
    }

    /**
     * 更新为取消
     * @return
     */
    private Boolean updateReseved(Long id) {
        return  pointGrantRepository.updateGrantRecordReserveState(
            id,
            AcBookRecordReserveState.RESERVE_PROCESSING,
            AcBookRecordReserveState.RESERVED
        );
    }

}
