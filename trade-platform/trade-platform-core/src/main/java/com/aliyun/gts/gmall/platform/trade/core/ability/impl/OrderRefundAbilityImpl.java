package com.aliyun.gts.gmall.platform.trade.core.ability.impl;


import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderRefundAbility;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToRefundData;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 正向退款实现
 * @author yangl
 */
@Component
@Slf4j
public class OrderRefundAbilityImpl implements OrderRefundAbility {

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private CancelReasonFeeRepository cancelReasonFeeRepository;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderPushAbility orderPushAbility;

    /**
     *  是否退运费
     * @param mainOrder
     */
    @Override
    public boolean isReturnFee(MainOrder mainOrder) {
        Boolean isReturnFee = false;
        //一个主单只能退一次运费 不能超额
        if(!mainOrder.orderAttr().getFreightRefunded()){
            TcCancelReasonFeeDO cancelReasonFee = new TcCancelReasonFeeDO();
            cancelReasonFee.setCancelReasonCode(mainOrder.getOrderAttr().getReasonCode());
            if(!StringUtils.isEmpty(mainOrder.getOrderAttr().getReasonCode())){
                TcCancelReasonFeeDO reasonFeeDO = cancelReasonFeeRepository.queryTcCancelReasonFee(cancelReasonFee);
                if(reasonFeeDO != null){
                    if(TradeExtendKeyConstants.FREIGHT_FEE_BELONG_CUST == reasonFeeDO.getCustFee()
                    && NumUtils.getNullZero(mainOrder.getOrderPrice().getFreightAmt())>=0 ){
                        isReturnFee = true;
                    }
                }
            }
        }
        return isReturnFee;
    }

    @Override
    public void doRefund(OrderStatus orderStatus,MainOrder mainOrder){
        MainOrder order = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        //取消订单给卖家发送email, 给买家发PUSH
        //orderPushAbility.sendOrderCancel(order);
        // 调用支付退款接口
        ToRefundData refundResult = this.toRefund(order);
        if(StringUtils.isBlank(refundResult.getRefundId())){
            log.info("refund failed mainOrder={}", order);
            //判断是否可退运费 更新标志
            if(this.isReturnFee(order)){
                this.updateOrderAttrRefund(order);
            }
            orderStatus.setStatus(OrderStatusEnum.CANCEL_FAILED);
            orderStatus.setCheckStatus(OrderStatusEnum.CANCEL_REQUESTED);
        }else {
            //退款成功
            log.info("refund success mainOrder={}", order);
            orderStatus.setStatus(OrderStatusEnum.CANCELLED);
            orderStatus.setCheckStatus(OrderStatusEnum.CANCEL_REQUESTED);
        }
    }

    public ToRefundData toRefund(MainOrder mainOrder) {
        ToRefundData refundResult = new ToRefundData();
        //判断是否预售订单
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                if (StepOrderUtils.isPaid(stepOrder)) {
                    refundResult = orderPayRepository.createRefund(mainOrder, stepOrder.getStepNo());
                }
            }
        }else{//正常退款
            refundResult = orderPayRepository.createRefund(mainOrder, null);
        }
        return refundResult;
    }

    /**
     * 更新退运费标志
     * @param mainOrder
     */
    public void updateOrderAttrRefund(MainOrder mainOrder) {
        // 退款原因
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        orderAttrDO.setFreightRefunded(true);
        // 变更订单的信息
        TcOrderDO newTcorderDO = new TcOrderDO();
        newTcorderDO.setOrderAttr(orderAttrDO);
        newTcorderDO.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        newTcorderDO.setOrderId(mainOrder.getPrimaryOrderId());
        // 更新订单
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

}
