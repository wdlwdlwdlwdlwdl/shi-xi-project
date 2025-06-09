package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ToRefundMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.ReversalPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.pay.AfterRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderRollCouponService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayRefundDomainService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToRefundData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.*;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PayRefundDomainServiceImpl implements PayRefundDomainService {

    @Autowired
    private ReversalProcessAbility reversalProcessAbility;

    @Autowired
    private AfterRefundAbility afterRefundAbility;

    @Autowired
    private ReversalQueryService reversalQueryService;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TransactionProxy transactionProxy;

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Autowired
    private ReversalConverter reversalConverter;

    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @Autowired
    private TcReversalFlowRepository tcReversalFlowRepository;

    @Autowired
    private ReversalPushAbility reversalPushAbility;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OrderRollCouponService orderRollCouponService;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Override
    public boolean payRefundExecute(RefundSuccessMessage message) throws Exception {
        MainReversal mainReversal = reversalQueryService.queryReversal(
            Long.parseLong(message.getPrimaryReversalId()),
            ReversalDetailOption.builder().includeOrderInfo(true).build()
        );
        reversalProcessAbility.refundSuccess(mainReversal, message.getStepNo());
        log.info("应进入售后单库存回补流程：{}",mainReversal.toString());
        if (ReversalStatusEnum.REVERSAL_OK.getCode().equals(mainReversal.getReversalStatus())) {
            TradeBizResult result = afterRefundAbility.afterRefund(mainReversal);
            return result.isSuccess();
        }
        return true;
    }

    /**
     * 退单退款
     * @param message
     * @throws Exception
     */
    @Override
    public void toRefundExecute(ToRefundMessageDTO message) throws Exception {
        // 通过退单加锁
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.REFUND_ORDER_LOCK_KEY, message.getPrimaryReversalId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new GmallException(ReversalErrorCode.REVERSAL_ORDER_LOCKED);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(ReversalErrorCode.REVERSAL_ORDER_LOCKED);
        }
        try {
            // 查询下退单记录
            MainReversal mainReversal = reversalQueryService.queryReversal(
                message.getPrimaryReversalId(),
                ReversalDetailOption.builder().includeOrderInfo(true).build()
            );
            if (Objects.isNull(mainReversal)) {
                return;
            }
            // 发起退款退款计算
            ToRefundData refundResult = new ToRefundData();
            if (StepOrderUtils.isMultiStep(mainReversal)) {
                for (Integer step : StepOrderUtils.getSteps(mainReversal)) {
                    refundResult = orderPayRepository.createRefund(mainReversal, step);
                }
            } else {
                refundResult = orderPayRepository.createRefund(mainReversal, null);
            }
            ReversalStatusEnum targetStatus;
            if(StringUtils.isBlank(refundResult.getRefundId())){
                log.info("refund failed mainReversal={}",mainReversal);
                //退款失败
                targetStatus = ReversalStatusEnum.REFUND_FAILED;
                // 保存
                ReversalUpdate up = ReversalUpdate.builder()
                    .status(targetStatus)
                    .updateFeatures(true)
                    .refuseTime(new Date())
                    .build();
                List<TcReversalDO> upList = getUpList(mainReversal, up);
                transactionProxy.callTx(() -> {
                    // 更新退单
                    updateByReversalId(upList);
                    // 更新订单转台
                    updateOrderStatus(mainReversal, targetStatus);
                    //售后单操作状态流程添加
                    addFlow(mainReversal, targetStatus.getCode(),false);
                });
            } else {
                //退款成功
                log.info("refund success mainReversal={}", mainReversal);
                List<Long> orderIds = mainReversal.getSubReversals().stream().map(p->p.getSubOrder().getOrderId()).toList();
                List<TcOrderDO> orderList = tcOrderRepository.queryOrdersByPrimaryId(mainReversal.getMainOrder().getPrimaryOrderId());
                //退款成功之后，判断是否是部分退
                TcOrderDO tcOrderDO = orderList.stream()
                    .filter(p-> !orderIds.contains(p.getOrderId()))
                    .filter(p-> p.getPrimaryOrderFlag() != 1)
                    .filter(p-> (!p.getOrderStatus().equals(ReversalStatusEnum.REFUND_PART_SUCCESS.getCode()))
                            &&!p.getOrderStatus().equals(ReversalStatusEnum.REFUND_FULL_SUCCESS.getCode()))
                    .findFirst()
                    .orElse(null);
                targetStatus = tcOrderDO != null ? ReversalStatusEnum.REFUND_PART_SUCCESS : ReversalStatusEnum.REFUND_FULL_SUCCESS;
                // 保存
                ReversalUpdate up = ReversalUpdate.builder()
                    .status(targetStatus)
                    .updateFeatures(true)
                    .refuseTime(new Date())
                    .reversalCompletedTime(new Date()).build();
                List<TcReversalDO> upList = getUpList(mainReversal, up);
                transactionProxy.callTx(() -> {
                    // 更新退单
                    updateByReversalId(upList);
                    // 更新订单转台
                    updateOrderStatus(mainReversal, targetStatus);
                    //售后单操作状态流程添加
                    addFlow(mainReversal,targetStatus.getCode(),false);
                });
            }
            //再次查最新订单状态
            MainReversal newMainReversal = reversalQueryService.queryReversal(
                message.getPrimaryReversalId(),
                ReversalDetailOption.builder().includeOrderInfo(true).build()
            );
            // 发PUSH
            reversalPushAbility.send(newMainReversal, ImTemplateTypeEnum.PUSH.getCode());
            // 退券
            if (ReversalStatusEnum.REFUND_FULL_SUCCESS.equals(targetStatus)) {
                // 查询下
                MainOrder mainOrder = orderQueryAbility.getMainOrder(mainReversal.getMainOrder().getPrimaryOrderId());
                if (Objects.nonNull(mainOrder)) {
                    orderRollCouponService.orderRollCoupon(mainOrder);
                }
            }
        } finally {
            orderLock.unLock();
        }
    }

    /**
     * 写流水
     * @param re
     * @param status
     * @param isCustomer
     */
    protected void addFlow(MainReversal re, Integer status, boolean isCustomer) {
        TcReversalFlowDO flow = new TcReversalFlowDO();
        flow.setPrimaryReversalId(re.getPrimaryReversalId());
        flow.setFromReversalStatus(re.getReversalStatus());
        flow.setToReversalStatus(status);
        flow.setCustOrSeller(isCustomer ? 1 : 0);
        flow.setOpName(OrderStatusEnum.codeOf(status).getInner());
        flow.setGmtCreate(new Date());
        tcReversalFlowRepository.insert(flow);
    }

    /**
     * 更新退单 状态
     * @param upList
     * 2025-3-17 17:41:55
     */
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
     * 更新订单状态
     * @param reversal
     * @param orderStatus
     */
    protected void updateOrderStatus(MainReversal reversal,ReversalStatusEnum orderStatus) {
        tcOrderRepository.reversalStatusSynOrder(
            reversal,reversal.getMainOrder().getPrimaryOrderId(),
            reversal.getReversalStatus(),orderStatus.getCode()
        );
    }

    protected void addLogistics(List<TcLogisticsDO> list) {
        for (TcLogisticsDO logis : list) {
            tcLogisticsRepository.create(logis);
        }
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
        Date reversalCompletedTime;
    }

    protected List<TcReversalDO> getUpList(MainReversal re, ReversalUpdate update) {
        List<TcReversalDO> upList = new ArrayList<>();
        upList.add(buildMainUp(re, update));
        for (SubReversal sub : re.getSubReversals()) {
            upList.add(buildSubUp(sub, update));
        }
        return upList;
    }

    protected TcReversalDO buildMainUp(MainReversal re, ReversalUpdate update) {
        TcReversalDO up = new TcReversalDO();
        up.setReversalId(re.getPrimaryReversalId());
        up.setReversalStatus(update.status.getCode());
        up.setSellerMemo(update.sellerMemo);
        up.setSellerMedias(update.sellerMedias);
        up.setReversalFeatures(buildFeature(update, re.getReversalFeatures(), true));
        up.setVersion(re.getVersion());
        if(update.reversalCompletedTime != null){
            up.setReversalCompletedTime(update.reversalCompletedTime);
        }
        return up;
    }

    protected TcReversalDO buildSubUp(SubReversal re, ReversalUpdate update) {
        TcReversalDO up = new TcReversalDO();
        up.setReversalId(re.getReversalId());
        up.setReversalStatus(update.status.getCode());
        up.setReversalFeatures(buildFeature(update, re.getReversalFeatures(), false));
        up.setVersion(re.getVersion());
        if(update.reversalCompletedTime != null){
            up.setReversalCompletedTime(update.reversalCompletedTime);
        }
        return up;
    }


    protected ReversalFeatureDO buildFeature(ReversalUpdate update, ReversalFeatureDO features, boolean isMain) {
        if (!update.updateFeatures) {
            return null;
        }
        // 子售后单feature 上都不做记录
        if (!isMain) {
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
}
