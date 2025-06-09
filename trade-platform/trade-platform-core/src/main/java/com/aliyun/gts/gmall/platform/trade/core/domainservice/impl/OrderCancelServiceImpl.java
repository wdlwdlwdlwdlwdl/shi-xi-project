package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderStatusAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.*;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderOperateFlowQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderCancelServiceImpl implements OrderCancelService {

    @Autowired
    private OrderStatusAbility orderStatusAbility;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Autowired
    private TransactionProxy transactionProxy;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderRollCouponService orderRollCouponService;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private OrderRefundAbility orderRefundAbility;

    @Autowired
    private OrderAutoCancelConfigService orderAutoCancelConfigService;

    public TradeBizResult<List<TcOrderDO>> autoCancel(OrderStatus orderStatus) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        //PARTIALLY_PAID
        //WAITING FOR PAYMENT
        //PAYMENT CONFIRMED
        //ACCEPTED_BY _MERCHANT
        //DELIVERY TO DC
        //WAITING_FOR_COURIER
        //DELIVERY 以上订单状态支持自动取消
        Boolean refund = true;
        Integer primaryOrderStatus = mainOrder.getPrimaryOrderStatus();
        if(Objects.equals(primaryOrderStatus, OrderStatusEnum.WAITING_FOR_PAYMENT.getCode())){
            orderStatus.setStatus(OrderStatusEnum.CANCELLED);
            orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_PAYMENT);
            refund = false;
        } else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.PARTIALLY_PAID.getCode())){
            orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
            orderStatus.setCheckStatus(OrderStatusEnum.PARTIALLY_PAID);
        } else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.PAYMENT_CONFIRMED.getCode())){
            orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
            orderStatus.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMED);
        }else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode())){
            orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
            orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
        }else{
            orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
            orderStatus.setCheckStatus(OrderStatusEnum.codeOf(primaryOrderStatus));
        }
        orderStatus.setCancelFromStatus(OrderStatusEnum.codeOf(primaryOrderStatus));
        // 订单扩展字段
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        // 自动退单的原因说明
        orderAttrDO.setRemark("");
        // 根据订单状态读取配置表，找到对应的取消原因
        orderAttrDO.setReasonCode(orderAutoCancelConfigService.getCancelCodeByOrderStatus(primaryOrderStatus));
        TcOrderDO newTcorderDO = new TcOrderDO();
        newTcorderDO.setOrderAttr(orderAttrDO);
        newTcorderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setVersion(mainOrder.getVersion());
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        if(Boolean.TRUE.equals(refund)){
            //首先更新CANCEL_REQUESTED
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                orderStatus.getPrimaryOrderId(),
                orderStatus.getStatus().getCode(),
                orderStatus.getOrderStage(),
                orderStatus.getCheckStatus().getCode()
            );
            addFlow(mainOrder, OrderStatusEnum.CANCEL_REQUESTED.getCode(), OrderStatusEnum.CANCEL_REQUESTED.getInner(),false);
            //调用退款
            orderRefundAbility.doRefund(orderStatus, mainOrder);
        }
        // 退券
        orderRollCouponService.orderRollCoupon(mainOrder);
        // 更新订单状态
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
    }

    /**
     * 写操作流水
     * @param mainOrder
     * @param targetStatus
     * @param name
     * @param isCustomer
     */
    protected void addFlow(MainOrder mainOrder, Integer targetStatus, String name, boolean isCustomer) {
        List<TcOrderOperateFlowDO> flows = new ArrayList<>();
        for(SubOrder subOrder : mainOrder.getSubOrders()){
            OrderOperateFlowQuery query = new OrderOperateFlowQuery();
            query.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            query.setFromOrderStatus(mainOrder.getPrimaryOrderStatus());
            query.setToOrderStatus(targetStatus);
            if(!tcOrderOperateFlowRepository.exist(query)) {
                TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
                flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                flow.setOrderId(subOrder.getOrderId());
                flow.setFromOrderStatus(mainOrder.getPrimaryOrderStatus());
                flow.setToOrderStatus(targetStatus);
                flow.setOperatorType(isCustomer ? 1 : 0);
                flow.setGmtCreate(new Date());
                flow.setOpName(name);
                flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
                flow.setGmtModified(new Date());
                flows.add(flow);
            }
        }
        tcOrderOperateFlowRepository.batchCreate(flows);
    }

}
