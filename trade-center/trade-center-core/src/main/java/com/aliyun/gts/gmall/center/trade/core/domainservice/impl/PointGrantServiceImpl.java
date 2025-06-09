package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PointGrantService;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantConfig;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointGrantParam;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PointGrantRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.promotion.common.constant.AcBookRecordReserveState;
import com.aliyun.gts.gmall.platform.promotion.common.util.DateUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 赠送积分退回时, 可退成负数
 * 如需不退成负数, 使用v2实现
 */
@Service
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "pointGrantService",
        havingValue = "default", matchIfMissing = true)
public class PointGrantServiceImpl implements PointGrantService {
    private static final String KEY_CONFIG = "grantPointConfig";
    private static final String KEY_COUNT = "grantPointCount";
    protected static final String KEY_RETURN = "grantPointReturn";
    private static final String LOCK_NAME = "PointGrant_";
    private static final int LOCK_WAIT = 2000;
    private static final int LOCK_MAX = 20000;

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderExtraService orderExtraService;
    @Autowired
    private PointGrantRepository pointGrantRepository;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private TcReversalRepository tcReversalRepository;
    @Autowired
    private CacheManager tradeCacheManager;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;

    @Override
    public void grantOnOrderCreate(MainOrder mainOrder) {
        PointGrantConfig g = pointGrantRepository.getGrantConfig();
        if (!g.isTradeGrantPoint()) {
            return; // 不赠积分
        }

        // fixed: 后面可能改价,以及多阶段有后续支付，这里都记录积分规则
//        Long realFee = mainOrder.getCurrentPayInfo().getPayPrice().getOrderRealAmt();
//        if (realFee <= 0) {
//            return; // 实付为0, 不赠积分
//        }

        PointGrantConfig exist = queryFromOrder(mainOrder.getPrimaryOrderId());
        if (exist != null) {
            return; // 已经记录过了
        }

        // 保存扩展结构
        Map<String, String> extend = new HashMap<>();
        extend.put(KEY_CONFIG, JSON.toJSONString(g));
        Map<String, Map<String, String>> extendMap = new HashMap<>();
        extendMap.put(KEY_CONFIG, extend);

        OrderExtraSaveRpcReq save = new OrderExtraSaveRpcReq();
        save.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        save.setAddExtends(extendMap);
        orderExtraService.saveOrderExtras(save);
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

    @Override
    public void grantOnOrderSuccess(Long primaryOrderId) {
        PointGrantConfig g = queryFromOrder(primaryOrderId);
        if (g == null || !g.isTradeGrantPoint()) {
            return; // 不赠积分
        }
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        long confirmRealAmt = calConfirmRealAmt(mainOrder);
        // 根据确认订单实付金额, 计算赠积分个数
        Long grantPointCount = calcGrantPoint(confirmRealAmt, g);
        if (grantPointCount == null || grantPointCount.longValue() <= 0) {
            return; // 不赠积分
        }
        // 调用积分赠送
        Date endDate = pointGrantRepository.calcInvalidDate(g, new Date());
        PointGrantParam param = new PointGrantParam();
        param.setCustId(mainOrder.getCustomer().getCustId());
        param.setMainOrderId(mainOrder.getPrimaryOrderId());
        param.setCount(grantPointCount);
        param.setInvalidDate(endDate);
        if (g.getGrantPointReserveDay() == null || g.getGrantPointReserveDay() <= 0) {
            //走原有逻辑，不变，reserve_state塞0，不需要保留
            param.setReserveState(AcBookRecordReserveState.NOT_RESERVE);
        }
        else {
            param.setReserveState(AcBookRecordReserveState.RESERVED);
            param.setEffectTime(DateUtils.add(new Date(), g.getGrantPointReserveDay()));
        }
        param.setRemark(I18NMessageUtils.getMessage("normal.order") +  mainOrder.getPrimaryOrderId() + I18NMessageUtils.getMessage("points.award"));
        pointGrantRepository.grantPoint(param);
        // 记录赠送数量
        Map<String, String> feature = new HashMap<>();
        feature.put(KEY_COUNT, String.valueOf(grantPointCount));
        OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setAddFeatures(feature);
        orderExtraService.saveOrderExtras(req);
    }

    @Override
    public void rollbackOnReversalSuccess(Long primaryReversalId) {
        rollbackPointGrant(primaryReversalId);
    }

    @Override
    public boolean rollbackBeforeRefund(Long primaryReversalId) {
        // 不处理
        return true;
    }

    protected void rollbackPointGrant(Long primaryReversalId) {
        MainReversal reversal = reversalQueryService.queryReversal(primaryReversalId,
                ReversalDetailOption.builder().includeOrderInfo(true).build());
        if (reversal == null) {
            return;
        }

        // 对主订单加锁，当所退完情况最后一个售后单有特殊逻辑
        String lockKey = LOCK_NAME + reversal.getMainOrder().getPrimaryOrderId();
        DistributedLock lock = tradeCacheManager.getLock(lockKey);
        boolean b;
        try {
            b = lock.tryLock(LOCK_WAIT, LOCK_MAX, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (b) {
            try {
                doRollbackOnReversalSuccess(reversal);
            } finally {
                lock.unLock();
            }
        } else {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

    private void doRollbackOnReversalSuccess(MainReversal reversal) {
        ReversalFeatureDO reversalFeatures = reversal.reversalFeatures();
        String value = reversalFeatures.getExtend(KEY_RETURN);
        Long returnCount;

        // 已经计算了, 重试的情况
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            returnCount = Long.parseLong(value);
        }
        // 计算退赠积分数量
        else {
            returnCount = calcReturnPoint(reversal);
            if (returnCount == null) {
                return;
            }
            reversalFeatures.putExtend(KEY_RETURN, String.valueOf(returnCount));
            TcReversalDO up = new TcReversalDO();
            up.setReversalId(reversal.getPrimaryReversalId());
            up.setVersion(reversal.getVersion());
            up.setReversalFeatures(reversalFeatures);
            boolean success = tcReversalRepository.updateByReversalIdVersion(up);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            reversal.setVersion(up.getVersion());   // 后面再次update db 时需要
        }

        // 退积分
        if (returnCount > 0) {
            PointRollbackParam param = new PointRollbackParam();
            param.setCustId(reversal.getCustId());
            param.setMainReversalId(reversal.getPrimaryReversalId());
            param.setMainOrderId(reversal.getMainOrder().getPrimaryOrderId());
            param.setCount(returnCount);
            callRollbackPointGrant(param, reversal);
        }
    }

    protected void callRollbackPointGrant(PointRollbackParam param, MainReversal reversal) {
        pointGrantRepository.rollbackGrantPoint(param);
    }

    private Long calcReturnPoint(MainReversal reversal) {
        String value = reversal.getMainOrder().getOrderAttr().getExtend(KEY_COUNT);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        Long cancelFee = reversal.getReversalFeatures().getCancelRealAmt();
        if (cancelFee == null || cancelFee.longValue() <= 0) {
            return null;
        }

        // 发放数量
        long grantPointCount = Long.parseLong(value);
        long confirmRealAmt = calConfirmRealAmt(reversal.getMainOrder());

        // 历史已退
        long historyReturnCount = 0L;
        long historyReturnAmt = 0L;
        List<MainReversal> historyList = reversalQueryService.queryReversalByOrder(reversal.getMainOrder().getPrimaryOrderId());
        for (MainReversal history : historyList) {
            if (history.getPrimaryReversalId().longValue() == reversal.getPrimaryReversalId().longValue()
                    || (!ReversalStatusEnum.REVERSAL_OK.getCode().equals(history.getReversalStatus())
                        && !ReversalStatusEnum.WAIT_REFUND.getCode().equals(history.getReversalStatus()))) {
                continue;
            }
            // 只保留确认收货后的退款
            if (!OrderStageEnum.AFTER_SALE.getCode().equals(history.reversalFeatures().getOrderStage())) {
                continue;
            }
            historyReturnAmt += history.reversalFeatures().getCancelRealAmt();
            String hisValue = history.reversalFeatures().getExtend(KEY_RETURN);
            try {
                long count = Long.parseLong(hisValue);
                historyReturnCount += count;
            } catch (NumberFormatException e) {
                log.error("reversal error: " + history.getPrimaryReversalId(), e);
            }
        }

        // 全部退完
        if (historyReturnAmt + cancelFee == confirmRealAmt) {
            return grantPointCount - historyReturnCount;
        }
        // 按比例退
        else {
            long toReturnCount = grantPointCount * cancelFee.longValue() / confirmRealAmt;
            return adjustPointCountUpper(toReturnCount, grantPointCount - historyReturnCount);
        }
    }

    // 积分数量换算金额 除不尽时, 向上调整积分数量
    private long adjustPointCountUpper(long pointCount, long maxCount) {
        PointExchange ex = pointExchangeRepository.getExchangeRate();
        long amt = ex.exCountToAmtUpper(pointCount);
        long newCount = ex.exAmtToCount(amt);
        return Math.min(newCount, maxCount);
    }

    private Long calcGrantPoint(Long realFee, PointGrantConfig g) {
        if (realFee == null || realFee <= 0L) {
            return null;
        }
        return (realFee.longValue() / 100) * g.getGrantPointOneYuan();
    }
}
