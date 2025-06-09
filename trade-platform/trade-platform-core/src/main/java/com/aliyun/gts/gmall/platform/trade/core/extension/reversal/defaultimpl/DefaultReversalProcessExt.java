package com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderStatusAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.pay.AfterRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalMessageAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.ReversalReceiveTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.ReversalTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.*;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToRefundData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.*;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultReversalProcessExt implements ReversalProcessExt {

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Autowired
    private ReversalQueryService reversalQueryService;

    @Autowired
    private ReversalConverter reversalConverter;

    @Autowired
    private TransactionProxy transactionProxy;

    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @Autowired
    private ReversalMessageAbility reversalMessageAbility;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TcReversalFlowRepository tcReversalFlowRepository;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private ReversalReceiveTask reversalReceiveTask;

    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderStatusAbility orderStatusAbility;

    @Autowired
    private ReversalProcessAbility reversalProcessAbility;

    @Autowired
    private AfterRefundAbility afterRefundAbility;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void sellerAgree(ReversalAgreeRpcReq req, MainReversal reversal)  {
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, reversal.getMainOrder().getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        try {
            // 查询、校验状态
            checkStatus(reversal, ReversalStatusEnum.WAITING_FOR_ACCEPT);
            //校验所属
            checkOwner(reversal, req, false);
            // 修改以为等待退回状态
            ReversalStatusEnum targetStatus = ReversalStatusEnum.WAITING_FOR_RETURN;
            // 保存
            ReversalUpdate reversalUpdate = ReversalUpdate.builder()
                .status(targetStatus)
                .receiver(req.getReceiver())
                .updateFeatures(true)
                .agreeTime(new Date())
                .build();
            List<TcReversalDO> upList = getUpList(reversal, reversalUpdate);
            // 开启事务
            transactionProxy.callTx(() -> {
                // 更新退单状态
                updateByReversalId(upList);
                // 修改订单状态
                updateOrderStatus(reversal, targetStatus);
                // 写流水
                addFlow(reversal, targetStatus, targetStatus.getInner(), false);
            });
            MainReversal newReversal = reversalQueryService.queryReversal(
                    req.getPrimaryReversalId(),
                    ReversalDetailOption.builder().includeOrderInfo(true).build()
            );
            // 生成退单任务
            orderTaskAbility.reversalTask(newReversal, targetStatus.getCode());
            // 状态修改方法
            reversalMessageAbility.sendStatusChangedMessage(newReversal, targetStatus);
        } finally {
            orderLock.unLock();
        }
    }

    //是否需要物流, 需要的卖家同意后消费者需寄回商品
    protected boolean needLogistics(MainReversal reversal) {
        Boolean b = reversal.getReversalFeatures().getNeedLogistics();
        if (b != null) {
            return b.booleanValue();
        }
        return false;
    }

    /**
     * 卖家不同意售后,并关闭
     * @param req
     * @param reversal
     */
    @Override
    public void sellerRefuse(ReversalRefuseRpcReq req, MainReversal reversal) {
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, reversal.getMainOrder().getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        try {
            // 针对商家待接收  运营复核场景取消
            //拒绝回到主订单之前状态
            TcReversalFlowDO tcReversalFlowDO = tcReversalFlowRepository.queryReversalFlow(req.getPrimaryReversalId());
            if (Objects.isNull(tcReversalFlowDO)) {
                throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
            }
            // 最终状态
            ReversalStatusEnum targetStatus = tcReversalFlowDO.getFromReversalStatus().intValue() == ReversalStatusEnum.COMPLETED.getCode() ?
                ReversalStatusEnum.COMPLETED : ReversalStatusEnum.REFUND_PART_SUCCESS;
            // 写流水 写重复了
            // addFlow(reversal, ReversalStatusEnum.CANCEL_REQUESTED, ReversalStatusEnum.CANCEL_REQUESTED.getInner(), false);
            // 保存
            ReversalUpdate reversalUpdate = ReversalUpdate.builder()
                .status(ReversalStatusEnum.CANCELLED)
                .sellerMemo(req.getSellerMemo())
                .sellerMedias(req.getSellerMedias())
                .updateFeatures(true)
                .refuseTime(new Date()).build();

            List<TcReversalDO> upList = getUpList(reversal, reversalUpdate);
            List<TcOrderDO> ordUpList = getOrdUpOnClose(reversal);
            transactionProxy.callTx(() -> {
                // 更新退单状态
                updateByReversalId(upList);
                // 修改订单状态
                updateOrderStatus(reversal, targetStatus);
                // 写退单流水
                addFlow(reversal, ReversalStatusEnum.CANCELLED, ReversalStatusEnum.CANCELLED.getInner(), false);//#"卖家拒绝"
                //TODO C端 订单流水 也要写!!没做
            });
            // 状态修改方法
            reversalMessageAbility.sendStatusChangedMessage(reversal, targetStatus);
        } finally {
            orderLock.unLock();
        }
    }


    @Override
    public void customerCancel(ReversalModifyRpcReq req, MainReversal reversal) {
        // 查询、校验
        checkStatus(reversal, ReversalStatusEnum.WAITING_FOR_ACCEPT);
        checkOwner(reversal, req, true);
        // 保存
        ReversalStatusEnum targetStatus ;//拒绝回到主订单之前状态
        TcReversalFlowDO tcReversalFlowDO = tcReversalFlowRepository.queryReversalFlow(req.getPrimaryReversalId());
        if(tcReversalFlowDO.getFromReversalStatus().intValue()==ReversalStatusEnum.COMPLETED.getCode()){
            targetStatus = ReversalStatusEnum.COMPLETED;
        }else {
            targetStatus = ReversalStatusEnum.REFUND_PART_SUCCESS;
        }
        ReversalUpdate up = ReversalUpdate.builder()
            .status(targetStatus)
            .updateFeatures(true)
            .cancelTime(new Date()).build();
        List<TcReversalDO> upList = getUpList(reversal, up);
        List<TcOrderDO> ordUpList = getOrdUpOnClose(reversal);
        transactionProxy.callTx(() -> {
            updateByReversalId(upList);
            updateByOrderId(ordUpList);
            addFlow(reversal, targetStatus, I18NMessageUtils.getMessage("cancelled"), true);//#"买家取消"
        });
        MainReversal newReversal = reversalQueryService.queryReversal(
                reversal.getPrimaryReversalId(),
                ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        reversalMessageAbility.sendStatusChangedMessage(newReversal, targetStatus);
    }

    /**
     * 系统默认取消
     * @param req
     * @param reversal
     */
    @Override
    public void cancelBySystem(ReversalModifyRpcReq req, MainReversal reversal) {
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, reversal.getMainOrder().getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        try {
            //TODO售后超时默认退款
            ReversalStatusEnum targetStatus = ReversalStatusEnum.CANCEL_REQUESTED;
            ReversalStatusEnum orderTargetStatus;
            Boolean refund = false;
            //根据最新需求 售后超时都不退款 20250318
           /* if(ReversalStatusEnum.WAITING_FOR_REFUND.getCode().equals(req.getReversalStatus()) ||
                ReversalStatusEnum.REFUND_REQUESTED.getCode().equals(req.getReversalStatus()) ||
                ReversalStatusEnum.REFUND_APPROVED.getCode().equals(req.getReversalStatus())) {
                //超时取消并打款客户 售后默认成功
                refund = true;
            } else {
                //超时取消售后
                refund = false;
            }*/
            // 写流水
            addFlow(reversal, ReversalStatusEnum.CANCEL_REQUESTED, ReversalStatusEnum.CANCEL_REQUESTED.getInner(), false);
            //调用售后退款接口
            if(refund){
                ToRefundData toRefundData = orderPayRepository.createRefund(reversal,null);
                if(StringUtils.isBlank(toRefundData.getRefundId())){
                    targetStatus = ReversalStatusEnum.CANCEL_FAILED;
                    orderTargetStatus = ReversalStatusEnum.REFUND_FAILED;
                } else {
                    targetStatus = ReversalStatusEnum.CANCELLED;
                    orderTargetStatus = ReversalStatusEnum.REFUND_FULL_SUCCESS;
                }
            } else {
                targetStatus = ReversalStatusEnum.CANCELLED;
                orderTargetStatus = ReversalStatusEnum.CANCELLED;
            }
            ReversalUpdate up = ReversalUpdate.builder()
                .status(targetStatus)
                .updateFeatures(true)
                .refuseTime(new Date())
                .build();
            List<TcReversalDO> upList = getUpList(reversal, up);
            List<TcOrderDO> ordUpList = getOrdUpOnClose(reversal);
            transactionProxy.callTx(() -> {
                // 更新退单状态
                updateByReversalId(upList);
                // 修改订单状态
                updateOrderStatus(reversal, orderTargetStatus);
                // 写流水
                addFlow(reversal, orderTargetStatus, orderTargetStatus.getInner(), false);
            });
            // 状态修改方法
            reversalMessageAbility.sendStatusChangedMessage(reversal, targetStatus);
        } finally {
            orderLock.unLock();
        }
    }

    @Override
    public void customerSendDeliver(ReversalDeliverRpcReq req, MainReversal reversal) {
        // 查询、校验
        checkStatus(reversal, ReversalStatusEnum.WAIT_DELIVERY);
        checkOwner(reversal, req, true);
        // 定时任务
        ScheduleTask task = reversalReceiveTask.buildTask(new ReversalTaskParam(
                reversal.getSellerId(),
                reversal.getMainOrder().getPrimaryOrderId(),
                reversal.getPrimaryReversalId(),
                BizCodeEntity.getOrderBizCode(reversal.getMainOrder())));

        // 保存
        ReversalStatusEnum targetStatus = ReversalStatusEnum.WAIT_CONFIRM_RECEIVE;
        List<String> nos = req.getLogisticsList().stream()
                .map(lo -> lo.getCompanyType() + "|" + lo.getLogisticsId())
                .collect(Collectors.toList());
        ReversalUpdate up = ReversalUpdate.builder()
                .status(targetStatus)
                .updateFeatures(true)
                .logisticsNos(nos)
                .autoReceiveTime(task != null ? task.getScheduleTime() : null)
                .sendTime(new Date()).build();
        List<TcReversalDO> upList = getUpList(reversal, up);
        List<TcLogisticsDO> logisticsList = new ArrayList<>();
        for (LogisticsInfoRpcReq logis : req.getLogisticsList()) {
            logisticsList.add(reversalConverter.toTcLogisticsDO(reversal, logis));
        }
        transactionProxy.callTx(() -> {
            updateByReversalId(upList);
            addLogistics(logisticsList);
            addFlow(reversal, targetStatus, I18NMessageUtils.getMessage("buyer.shipped"), true);//#"买家发货"
            orderTaskService.createScheduledTask(Collections.singletonList(task));
        });
        reversalMessageAbility.sendStatusChangedMessage(reversal, targetStatus);
    }

    /**
     * 卖家确认收货 -- 退货完成
     * @param req
     * @param reversal
     */
    @Override
    public void sellerConfirmDeliver(ReversalModifyRpcReq req, MainReversal reversal) {
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, reversal.getMainOrder().getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        try {
            // 查询、校验状态
            checkStatus(reversal, ReversalStatusEnum.WAITING_FOR_RETURN);
            //校验所属
            checkOwner(reversal, req, false);
            // 保存
            ReversalStatusEnum targetStatus = ReversalStatusEnum.WAITING_FOR_REFUND;
            ReversalUpdate up = ReversalUpdate.builder()
                .status(targetStatus)
                .updateFeatures(true)
                .confirmReceiveTime(new Date())
                .build();
            List<TcReversalDO> upList = getUpList(reversal, up);
            transactionProxy.callTx(() -> {
                // 更新退单状态
                updateByReversalId(upList);
                // 修改订单状态
                updateOrderStatus(reversal,targetStatus);
                // 写流水
                addFlow(reversal, targetStatus, targetStatus.getInner(), false);
                //TODO C端 订单流水 也要写!!没做
            });
            // 再查一次
            MainReversal newReversal = reversalQueryService.queryReversal(
                reversal.getPrimaryReversalId(),
                ReversalDetailOption.builder().includeOrderInfo(true).build()
            );
            // 生成退单任务
            orderTaskAbility.reversalTask(newReversal, targetStatus.getCode());
            // 发送状态
            reversalMessageAbility.sendStatusChangedMessage(newReversal, targetStatus);
        } finally {
            orderLock.unLock();
        }
    }

    @Override
    public void sellerRefuseDeliver(ReversalRefuseRpcReq req, MainReversal reversal) {
        // 查询、校验
        checkStatus(reversal, ReversalStatusEnum.WAIT_CONFIRM_RECEIVE);
        // 保存
        ReversalStatusEnum targetStatus = ReversalStatusEnum.SELLER_REFUSE;
        ReversalUpdate up = ReversalUpdate.builder()
            .status(targetStatus)
            .sellerMemo(req.getSellerMemo())
            .sellerMedias(req.getSellerMedias())
            .updateFeatures(true)
            .refuseTime(new Date()).build();
        List<TcReversalDO> upList = getUpList(reversal, up);
        List<TcOrderDO> ordUpList = getOrdUpOnClose(reversal);
        transactionProxy.callTx(() -> {
            updateByReversalId(upList);
            updateByOrderId(ordUpList);
            addFlow(reversal, targetStatus, I18NMessageUtils.getMessage("merchant.rejected.receipt"), false);//#"卖家拒绝收货"
        });
        reversalMessageAbility.sendStatusChangedMessage(reversal, targetStatus);
    }

    @Override
    public void refundSuccess(MainReversal reversal, Integer stepNo) {
        //多订单售后操作需研究
        log.info("售后主单详情：{}", reversal.toString());
        if ((Objects.equals(reversal.getReversalType(), ReversalTypeEnum.APPLY_CANCEL_REFUND.getCode())
                || Objects.equals(reversal.getReversalType(), ReversalTypeEnum.REFUND_ONLY.getCode())
                || Objects.equals(reversal.getReversalType(), ReversalTypeEnum.REFUND_ITEM.getCode()))
                && Objects.equals(reversal.getMainOrder().getPrimaryOrderStatus(), OrderStatusEnum.REVERSAL_DOING.getCode())) {
            //若需退款，售后单状态不用变依旧是退款中，支付单需变为待退款，订单需变为商家同意取消
            //修改应付应收单状态为"待退款"
            log.info("修改应付应收单状态为待退款");
            orderPayRepository.updatePayStatus(reversal.getMainOrder(), PayStatusEnum.PENDING_REFUND.getCode());

            //修改订单为商家同意取消
            log.info("修改订单为商家同意取消");
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setPrimaryOrderId(reversal.getMainOrder().getPrimaryOrderId());
            orderStatus.setStatus(OrderStatusEnum.SELLER_AGREE_CANCEL);
            orderStatus.setOrderStage(OrderStageEnum.ON_SALE.getCode());
            orderStatus.setCheckStatus(OrderStatusEnum.REVERSAL_DOING);
            orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(reversal.getMainOrder()));
        } else {
            // 多阶段, 多个退款单全部成功后, 才更新售后单
            if (StepOrderUtils.isMultiStep(reversal)) {
                log.info("多阶段, 多个退款单全部成功后, 才更新售后单");
                List<Integer> successSteps = reversal.reversalFeatures().successSteps();
                if (stepNo == null || successSteps.contains(stepNo)) {
                    return;
                }
                successSteps.add(stepNo);
                TcReversalDO up = new TcReversalDO();
                up.setPrimaryReversalId(reversal.getPrimaryReversalId());
                up.setReversalId(reversal.getPrimaryReversalId());
                up.setReversalFeatures(reversal.reversalFeatures());
                up.setVersion(reversal.getVersion());
                boolean b = tcReversalRepository.updateByReversalIdVersion(up);
                if (!b) {
                    throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
                }
                reversal.setVersion(up.getVersion());

                List<Integer> allSteps = StepOrderUtils.getSteps(reversal);
                if (!successSteps.containsAll(allSteps)) {
                    return;
                }
            }

            if (Objects.equals(reversal.getReversalType(), ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND.getCode())) {
                log.info("更新支付单状态为支付关闭");
                orderPayRepository.updatePayStatus(reversal.getMainOrder(), PayStatusEnum.PAY_CANCELED.getCode());
            }

            // 更新售后单状态
            TcReversalDO up = new TcReversalDO();
            up.setPrimaryReversalId(reversal.getPrimaryReversalId());
            up.setReversalStatus(ReversalStatusEnum.REVERSAL_OK.getCode());
            log.info("更新售后单状态");

            // 更新订单状态
            OrderStatusCalc orderStatus = getOrderStatusOnReversalFinish(reversal, true);
            log.info("更新订单状态");

            transactionProxy.callTx(() -> {
                boolean b = tcReversalRepository.updateByPrimaryReversalId(up, ReversalStatusEnum.WAIT_REFUND.getCode());
                if (b) {
                    updateByOrderId(orderStatus.upList);
                    orderStatus.updated = true;
                }
            });
            if (!orderStatus.updated) { // 多退款单并发调用, 只更新一次
                return;
            }
            // 订单变更消息
            if (orderStatus.orderSuccess) {
                List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());
                orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
                        .orderList(list)
                        .op(OrderChangeOperateEnum.SYS_CONFIRM_BY_REVERSAL)
                        .build());
            }
            // 发消息
            reversalMessageAbility.sendStatusChangedMessage(reversal, ReversalStatusEnum.REVERSAL_OK);
            reversal.setReversalStatus(up.getReversalStatus());
        }
    }

    @Override
    public void buyerConfirmRefund(MainReversal reversal) {
        TcReversalDO tcReversalDO = tcReversalRepository.queryByReversalId(reversal.getPrimaryReversalId());
        TcReversalDO update = new TcReversalDO();
        update.setReversalId(tcReversalDO.getPrimaryReversalId());
        update.setVersion(tcReversalDO.getVersion());
        ReversalFeatureDO featureDO = tcReversalDO.getReversalFeatures();
        if (featureDO == null) {
            featureDO = new ReversalFeatureDO();
        }
        featureDO.setBuyerConfirmRefund(true);
        featureDO.setBcrNumber(reversal.getBcrNumber());
        featureDO.setBcrMemo(reversal.getBcrMemo());

        update.setReversalFeatures(featureDO);
        tcReversalRepository.updateByReversalIdVersion(update);
        //TODO：需要加判断，只有本次需求才能进，正常的买家确认收款不能进
        if ((Objects.equals(reversal.getReversalType(), ReversalTypeEnum.APPLY_CANCEL_REFUND.getCode())
                || Objects.equals(reversal.getReversalType(), ReversalTypeEnum.REFUND_ONLY.getCode())
                || Objects.equals(reversal.getReversalType(), ReversalTypeEnum.REFUND_ITEM.getCode()))
                && Objects.equals(reversal.getMainOrder().getPrimaryOrderStatus(), OrderStatusEnum.SELLER_AGREE_CANCEL.getCode())) {
            // 若售后类型为申请取消-需退款且订单状态为商家同意取消时，售后单完成。调用refundSuccess
            //TODO:确定为何此处stepNo=-1?
            reversalProcessAbility.refundSuccess(reversal, -1);
            log.info("应进入售后单库存回补流程：{}", reversal);
            if (ReversalStatusEnum.REVERSAL_OK.getCode().equals(reversal.getReversalStatus())) {
                TradeBizResult result = afterRefundAbility.afterRefund(reversal);
            }
            log.info("更新支付单状态为支付关闭");
            orderPayRepository.updatePayStatus(reversal.getMainOrder(), PayStatusEnum.PAY_CANCELED.getCode());
        }
    }

    /**
     * 运营同意退款
     * @param req
     * @param reversal
     */
    @Override
    public void agreeByOperation(ReversalAgreeRpcReq req, MainReversal reversal) {
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, reversal.getMainOrder().getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
        }
        try {
            // 查询、校验 兼容运营人员复核
            ReversalStatusEnum targetStatus;
            if (Objects.equals(reversal.getReversalStatus(),ReversalStatusEnum.WAITING_FOR_REFUND.getCode())) {
                targetStatus = ReversalStatusEnum.REFUND_REQUESTED;
            } else if(Objects.equals(reversal.getReversalStatus(),ReversalStatusEnum.REFUND_REQUESTED.getCode())){
                targetStatus = ReversalStatusEnum.REFUND_APPROVED;
            } else {
                targetStatus = ReversalStatusEnum.REFUND_APPROVED;
            }
            // 保存
            ReversalUpdate up = ReversalUpdate.builder()
                .status(targetStatus)
                .receiver(req.getReceiver())
                .updateFeatures(true)
                .agreeTime(new Date()).build();
            List<TcReversalDO> upList = getUpList(reversal, up);
            transactionProxy.callTx(() -> {
                // 更新退单状态
                updateByReversalId(upList);
                // 更新订单状态
                updateOrderStatus(reversal, targetStatus);
                // 写流水
                addFlow(reversal, targetStatus, targetStatus.getInner(), false);
            });
            // 发消息
            reversalMessageAbility.sendStatusChangedMessage(reversal, targetStatus);
        }
        finally {
            orderLock.unLock();
        }
    }

    protected void createRefund(MainReversal reversal) {
        if (StepOrderUtils.isMultiStep(reversal)) {
            for (Integer step : StepOrderUtils.getSteps(reversal)) {
                orderPayRepository.createRefund(reversal, step);
            }
        } else {
            orderPayRepository.createRefund(reversal, null);
        }
        //异步返回退款结果
    }

    /**
     * 校验状态
     * @param reversal
     * @param expectStatus
     */
    protected void checkStatus(MainReversal reversal, ReversalStatusEnum... expectStatus) {
        ReversalStatusEnum status = ReversalStatusEnum.codeOf(reversal.getReversalStatus());
        for (ReversalStatusEnum expect : expectStatus) {
            if (status == expect) {
                return;
            }
        }
        throw new GmallException(ReversalErrorCode.REVERSAL_STATUS_ILLEGAL);
    }

    /**
     * 退单归属
     * @param reversal
     * @param req
     * @param isCustomer
     */
    protected void checkOwner(MainReversal reversal, ReversalModifyRpcReq req, boolean isCustomer) {
        if (isCustomer) {
            if (req.getCustId() == null) {
                throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("customer.id.required"));//#"顾客ID不能为空"
            }
            if (req.getCustId().longValue() != reversal.getCustId()) {
                throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
            }
        } else {
            if (req.getSellerId() == null) {
                throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("merchant.id.required"));//#"卖家ID不能为空"
            }
            if (req.getSellerId().longValue() != reversal.getSellerId()) {
                throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
            }
        }
    }

    protected void checkReceiver(ReceiverDTO receiver) {
        ParamUtil.nonNull(receiver, I18NMessageUtils.getMessage("shipping.address.required"));//#"收货地址不能为空"
        ParamUtil.nonNull(receiver.getDeliveryAddr(), I18NMessageUtils.getMessage("shipping.address.required"));//#"收货地址不能为空"
        ParamUtil.nonNull(receiver.getPhone(), I18NMessageUtils.getMessage("contact.phone.required"));//#"联系电话不能为空"
        ParamUtil.nonNull(receiver.getReceiverName(), I18NMessageUtils.getMessage("receiver.required"));//#"收货人不能为空"
    }

    protected List<TcReversalDO> getUpList(MainReversal re, ReversalUpdate update) {
        List<TcReversalDO> upList = new ArrayList<>();
        upList.add(buildMainUp(re, update));
        for (SubReversal sub : re.getSubReversals()) {
            upList.add(buildSubUp(sub, update));
        }
        return upList;
    }

    protected List<TcOrderDO> getOrdUpOnClose(MainReversal re) {
        OrderStatusCalc orderStatus = getOrderStatusOnReversalFinish(re, false);
        return orderStatus.upList;
    }

    protected TcReversalDO buildMainUp(MainReversal re, ReversalUpdate update) {
        TcReversalDO up = new TcReversalDO();
        up.setReversalId(re.getPrimaryReversalId());
        up.setReversalStatus(update.status.getCode());
        up.setSellerMemo(update.sellerMemo);
        up.setSellerMedias(update.sellerMedias);
        up.setReversalFeatures(buildFeature(update, re.getReversalFeatures(), true));
        up.setVersion(re.getVersion());
        return up;
    }

    protected TcReversalDO buildSubUp(SubReversal re, ReversalUpdate update) {
        TcReversalDO up = new TcReversalDO();
        up.setReversalId(re.getReversalId());
        up.setReversalStatus(update.status.getCode());
        up.setReversalFeatures(buildFeature(update, re.getReversalFeatures(), false));
        up.setVersion(re.getVersion());
        return up;
    }

    protected ReversalFeatureDO buildFeature(ReversalUpdate update, ReversalFeatureDO features, boolean isMain) {
        if (!update.updateFeatures) {
            return null;
        }
        if (!isMain) {  // 子售后单feature 上都不做记录
            return null;
        }
        if (features == null) {
            features = new ReversalFeatureDO();
        }
        if (update.logisticsNos != null) {
            features.setLogisticsNos(update.logisticsNos);
        }
        if (update.agreeTime != null) {
            features.setAgreeTime(update.agreeTime);
        }
        if (update.sendTime != null) {
            features.setSendTime(update.sendTime);
        }
        if (update.confirmReceiveTime != null) {
            features.setConfirmReceiveTime(update.confirmReceiveTime);
        }
        if (update.refuseTime != null) {
            features.setRefuseTime(update.refuseTime);
        }
        if (update.cancelTime != null) {
            features.setCancelTime(update.cancelTime);
        }
        if (update.receiver != null) {
            features.setReceiver(reversalConverter.toReceiverInfoDO(update.receiver));
        }
        if (update.autoReceiveTime != null) {
            features.setAutoConfirmDeliverTime(update.autoReceiveTime);
        }
        return features;
    }

    @Builder
    protected static class ReversalUpdate {
        ReversalStatusEnum status;
        String sellerMemo;
        List<String> sellerMedias;

        // 以下features字段
        boolean updateFeatures;
        ReceiverDTO receiver;
        List<String> logisticsNos;
        Date agreeTime;
        Date sendTime;
        Date confirmReceiveTime;
        Date refuseTime;
        Date cancelTime;
        Date autoReceiveTime;
    }

    // 更新退单状态
    protected void updateByReversalId(List<TcReversalDO> upList) {
        for (TcReversalDO up : upList) {
            boolean updated = tcReversalRepository.updateByReversalIdVersion(up);
            if (!updated) {
                // 通常是并发更新
                throw new GmallException(CommonErrorCode.SERVER_ERROR);
            }
        }
    }

    /**
     * 订单状态修改
     * @param reversal
     * @param orderStatus
     */
    protected void updateOrderStatus(MainReversal reversal,ReversalStatusEnum orderStatus) {
        tcOrderRepository.reversalStatusSynOrder(
            reversal,
            reversal.getMainOrder().getPrimaryOrderId(),
            reversal.getReversalStatus(),
            orderStatus.getCode()
        );
    }

    protected void addLogistics(List<TcLogisticsDO> list) {
        for (TcLogisticsDO logis : list) {
            tcLogisticsRepository.create(logis);
        }
    }

    protected void updateByOrderId(List<TcOrderDO> ordUpList) {
        for (TcOrderDO up : ordUpList) {
            boolean success = tcOrderRepository.updateByOrderIdVersion(up);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }

    /**
     * 写退单流水
     * @param re
     * @param targetStatus
     * @param name
     * @param isCustomer
     */
    protected void addFlow(MainReversal re, ReversalStatusEnum targetStatus, String name, boolean isCustomer) {
        TcReversalFlowDO flow = new TcReversalFlowDO();
        flow.setPrimaryReversalId(re.getPrimaryReversalId());
        flow.setFromReversalStatus(re.getReversalStatus());
        flow.setToReversalStatus(targetStatus.getCode());
        flow.setCustOrSeller(isCustomer ? 1 : 0);
        flow.setOpName(name);
        flow.setGmtCreate(new Date());
        tcReversalFlowRepository.insert(flow);
    }

    // 售后结束, 计算订单状态
    protected OrderStatusCalc getOrderStatusOnReversalFinish(MainReversal re, boolean successOrClose) {
        Map<Long, TcOrderDO> resultMap = new HashMap<>();
        long primaryOrderId = re.getMainOrder().getPrimaryOrderId();
        Integer fromOrderStatus = re.getReversalFeatures().getOrderStatus();

        for (SubOrder sub : re.getMainOrder().getSubOrders()) {
            TcOrderDO up = new TcOrderDO();
            up.setPrimaryOrderId(sub.getPrimaryOrderId());
            up.setOrderId(sub.getOrderId());
            up.setVersion(sub.getVersion());
            up.setOrderStatus(sub.getOrderStatus());
            resultMap.put(sub.getOrderId(), up);
        }
        {
            TcOrderDO up = new TcOrderDO();
            up.setPrimaryOrderId(primaryOrderId);
            up.setOrderId(primaryOrderId);
            up.setVersion(re.getMainOrder().getVersion());
            resultMap.put(primaryOrderId, up);
        }

        // 记录时间
        Date now = new Date();
        for (SubReversal sub : re.getSubReversals()) {
            OrderAttrDO orderAttr = sub.getSubOrder().orderAttr();
            orderAttr.setReversalEndTime(now);
            TcOrderDO up = resultMap.get(sub.getSubOrder().getOrderId());
            up.setOrderAttr(orderAttr);
        }
        // 当前子订单状态

        // 售后关闭的, 订单状态回退
        if (!successOrClose) {
            for (SubReversal sub : re.getSubReversals()) {
                TcOrderDO up = resultMap.get(sub.getSubOrder().getOrderId());
                up.setOrderStatus(fromOrderStatus);
            }
        }

        // 售后成功的
        else {

            // 哪些订单完结
            Set<Long> finishOrders = getFinishOrdersOnSuccess(re);
            for (SubReversal sub : re.getSubReversals()) {
                long subOrderId = sub.getSubOrder().getOrderId();
                TcOrderDO up = resultMap.get(subOrderId);
                //若不需要退款则均视为订单完结
                if (finishOrders.contains(subOrderId)
                        || Objects.equals(re.getReversalType(), ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND.getCode())) {
                    // 完结
                    up.setOrderStatus(OrderStatusEnum.REVERSAL_SUCCESS.getCode());
                    OrderAttrDO orderAttr = sub.getSubOrder().orderAttr();
                    orderAttr.setOrderStage(OrderStageEnum.AFTER_SALE.getCode());
                    up.setOrderAttr(orderAttr);
                } else {
                    // 状态回退
                    up.setOrderStatus(fromOrderStatus);
                }
            }
        }

        // 主订单状态
        {
            boolean hasReversalDoing = false;       // 是否存在售后中
            boolean hasNotReversalSuccess = false;      // 是否存在非售后完结的
            for (TcOrderDO order : resultMap.values()) {
                if (order.getOrderId() == primaryOrderId) {
                    continue;   // 排除主订单自己
                }
                OrderStatusEnum orderStatus = OrderStatusEnum.codeOf(order.getOrderStatus());
                if (orderStatus == OrderStatusEnum.REVERSAL_DOING) {
                    hasReversalDoing = true;
                } else if (orderStatus != OrderStatusEnum.REVERSAL_SUCCESS) {
                    hasNotReversalSuccess = true;
                }
            }

            int primaryOrderStatus
                    = hasReversalDoing ? OrderStatusEnum.REVERSAL_DOING.getCode()
                    : hasNotReversalSuccess ? fromOrderStatus
                    : OrderStatusEnum.REVERSAL_SUCCESS.getCode();

            for (TcOrderDO up : resultMap.values()) {
                up.setPrimaryOrderStatus(primaryOrderStatus);
            }
            // 记录时间
            if (primaryOrderStatus != OrderStatusEnum.REVERSAL_DOING.getCode()) {
                OrderAttrDO orderAttr = re.getMainOrder().orderAttr();
                orderAttr.setReversalEndTime(now);
                TcOrderDO up = resultMap.get(primaryOrderId);
                up.setOrderAttr(orderAttr);
            }
            resultMap.get(primaryOrderId).setOrderStatus(primaryOrderStatus);
        }

        // 是否订单完结, 即 售中主单 变成 REVERSAL_SUCCESS 时, 剩余金额进行打款
        boolean orderSuccess = false;
        {
            OrderStatusEnum primaryStatus = OrderStatusEnum.codeOf(resultMap.get(primaryOrderId).getOrderStatus());
            OrderStatusEnum fromStatus = OrderStatusEnum.codeOf(fromOrderStatus);
            if ((fromStatus == OrderStatusEnum.ORDER_SENDED
                    || fromStatus == OrderStatusEnum.ORDER_WAIT_DELIVERY
                    || fromStatus == OrderStatusEnum.STEP_ORDER_DOING
                    || fromStatus == OrderStatusEnum.PARTIALLY_PAID)
                    && primaryStatus == OrderStatusEnum.REVERSAL_SUCCESS) {
                // 主订单完结
                orderSuccess = true;
                OrderAttrDO orderAttr = re.getMainOrder().getOrderAttr();
                if (orderAttr == null) {
                    orderAttr = new OrderAttrDO();
                }
                orderAttr.setOrderStage(OrderStageEnum.AFTER_SALE.getCode());
                resultMap.get(primaryOrderId).setOrderAttr(orderAttr);
            }
        }

        return new OrderStatusCalc(new ArrayList<>(resultMap.values()), orderSuccess);
    }

    // 售后成功时调用, 哪些订单需要完结(状态到售后完成, 当主单的所有子单完结时打款)
    protected Set<Long> getFinishOrdersOnSuccess(MainReversal re) {
        Set<Long> list = new HashSet<>();
        ReversalTypeEnum type = ReversalTypeEnum.codeOf(re.getReversalType());

        // 仅退款, 申请取消,判断是否退完
        if (type == ReversalTypeEnum.REFUND_ONLY || type == ReversalTypeEnum.APPLY_CANCEL_REFUND || type == ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND) {
            List<TcReversalDO> history = tcReversalRepository.queryByPrimaryOrderId(re.getMainOrder().getPrimaryOrderId());
            Set<Long> all = getRefundAllOrders(history, re);
            list.addAll(all);
        }

        // 退货的情况, 直接完结订单
        // 修改逻辑: 金额退完 或 件数退完, 才完结订单
        else if (type == ReversalTypeEnum.REFUND_ITEM) {
//            for (SubReversal sub : re.getSubReversals()) {
//                list.add(sub.getSubOrder().getOrderId());
//            }
            List<TcReversalDO> history = tcReversalRepository.queryByPrimaryOrderId(re.getMainOrder().getPrimaryOrderId());
            Set<Long> amtAll = getRefundAllOrders(history, re);
            Set<Long> qtyAll = getReturnAllQtyOrders(history, re);
            amtAll.addAll(qtyAll);
            list.addAll(amtAll);
        }

        // 其他扩展售后类型, 需要由扩展实现
        else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "unknow ReversalType : " + re.getReversalType());
        }

        return list;
    }

    // 返回金额退完的订单
    protected Set<Long> getRefundAllOrders(List<TcReversalDO> history, MainReversal current) {
        Map<Long, LongValue> cancelAmtMap = new HashMap<>();
        Map<Integer, Map<Long, LongValue>> stepCancelAmtMap = new HashMap<>();

        // 本次退额
        for (SubReversal sub : current.getSubReversals()) {
            cancelAmtMap.put(sub.getSubOrder().getOrderId(), new LongValue(sub.getCancelAmt()));
            if (MapUtils.isNotEmpty(sub.reversalFeatures().getStepRefundFee())) {
                for (Entry<Integer, RefundFee> en : sub.reversalFeatures().getStepRefundFee().entrySet()) {
                    Map<Long, LongValue> stepMap = CommUtils.getValue(stepCancelAmtMap, en.getKey(), HashMap::new);
                    stepMap.put(sub.getSubOrder().getOrderId(), new LongValue(en.getValue().getCancelTotalAmt()));
                }
            }
        }

        // 历史退额
        for (TcReversalDO historyRe : history) {
            ReversalStatusEnum reStatus = ReversalStatusEnum.codeOf(historyRe.getReversalStatus());
            if (reStatus != ReversalStatusEnum.REVERSAL_OK) {
                continue;
            }
            LongValue count = cancelAmtMap.get(historyRe.getOrderId());
            if (count != null) {
                count.value += historyRe.getCancelAmt();
            }
            if (MapUtils.isNotEmpty(historyRe.getReversalFeatures().getStepRefundFee())) {
                for (Entry<Integer, RefundFee> en : historyRe.getReversalFeatures().getStepRefundFee().entrySet()) {
                    Map<Long, LongValue> stepMap = CommUtils.getValue(stepCancelAmtMap, en.getKey(), HashMap::new);
                    LongValue stepCount = stepMap.get(historyRe.getOrderId());
                    if (stepCount != null) {
                        stepCount.value += en.getValue().getCancelTotalAmt();
                    }
                }
            }
        }
        Set<Long> result = new HashSet<>();
        for (SubReversal sub : current.getSubReversals()) {
            // 全退完
            long cancelAmt = cancelAmtMap.get(sub.getSubOrder().getOrderId()).value;
            long orderTotalAmt = sub.getSubOrder().getOrderPrice().getOrderTotalAmt().longValue();
            if (cancelAmt >= orderTotalAmt) {
                result.add(sub.getSubOrder().getOrderId());
                continue;
            }

            // 已支付的阶段退完
            if (StepOrderUtils.isMultiStep(current.getMainOrder())) {
                boolean stepRefundAll = true;
                for (StepOrder stepOrder : current.getMainOrder().getStepOrders()) {
                    if (!StepOrderUtils.isPaid(stepOrder)) {
                        continue;
                    }
                    Map<Long, LongValue> stepMap = stepCancelAmtMap.get(stepOrder.getStepNo());
                    long stepCancelAmt = stepMap == null ? 0L : stepMap.get(sub.getSubOrder().getOrderId()).value;
                    long stepTotalAmt = sub.getSubOrder().getOrderPrice().getStepAmt().get(stepOrder.getStepNo()).getTotalAmt();
                    if (stepCancelAmt < stepTotalAmt) {
                        stepRefundAll = false;
                        break;
                    }
                }
                if (stepRefundAll) {
                    result.add(sub.getSubOrder().getOrderId());
                }
            }
        }
        return result;
    }

    // 返回件数退完的订单
    protected Set<Long> getReturnAllQtyOrders(List<TcReversalDO> history, MainReversal current) {
        Map<Long, LongValue> cancelQtyMap = new HashMap<>();

        // 本次退额
        for (SubReversal sub : current.getSubReversals()) {
            cancelQtyMap.put(sub.getSubOrder().getOrderId(), new LongValue(sub.getCancelQty()));
        }

        // 历史退额
        for (TcReversalDO historyRe : history) {
            ReversalStatusEnum reStatus = ReversalStatusEnum.codeOf(historyRe.getReversalStatus());
            if (reStatus != ReversalStatusEnum.REVERSAL_OK) {
                continue;
            }
            LongValue count = cancelQtyMap.get(historyRe.getOrderId());
            if (count != null) {
                count.value += historyRe.getCancelQty();
            }
        }

        Set<Long> result = new HashSet<>();
        for (SubReversal sub : current.getSubReversals()) {
            // 全退完
            long cancelQty = cancelQtyMap.get(sub.getSubOrder().getOrderId()).value;
            long orderTotalQty = sub.getSubOrder().getOrderQty();
            if (cancelQty >= orderTotalQty) {
                result.add(sub.getSubOrder().getOrderId());
                continue;
            }
        }
        return result;
    }

    protected static class OrderStatusCalc {
        private final List<TcOrderDO> upList;
        private final boolean orderSuccess;
        private boolean updated;

        public OrderStatusCalc(List<TcOrderDO> upList, boolean orderSuccess) {
            this.upList = upList;
            this.orderSuccess = orderSuccess;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    protected static class LongValue {
        public long value;
    }

}
