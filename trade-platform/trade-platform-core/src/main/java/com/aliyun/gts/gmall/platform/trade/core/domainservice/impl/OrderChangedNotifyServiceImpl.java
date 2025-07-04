package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderMessageAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSuccessAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OrderChangedNotifyServiceImpl implements OrderChangedNotifyService {

    @Autowired
    private OrderMessageAbility orderMessageAbility;
    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderSuccessAbility orderSuccessAbility;

    @Override
    @Transactional
    public void afterStatusChange(OrderChangeNotify change) {
        if (change.getOp() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        // 订单完结, 计算分账金额
        if (change.getOp().isOrderSuccess()) {
            MainOrder mainOrder = change.getMainOrder();
            if (mainOrder == null) {
                Long primaryOrderId = change.getOrderList().get(0).getPrimaryOrderId();
                mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            }
            // 发订单完成消息
            orderSuccessAbility.processOrderSuccess(mainOrder);
        }
        // 记录操作流水表
        List<TcOrderOperateFlowDO> flows = change.getFlows();
        if (flows == null) {
            if (CollectionUtils.isNotEmpty(change.getOrderList())) {
                flows = getOpFlow(change.getOrderList(), change.getOp());
            } else if (change.getMainOrder() != null) {
                flows = getOpFlow(change.getMainOrder(), change.getOp());
            }
            if (flows != null && change.getFromOrderStatus() != null) {
                flows.forEach(f -> f.setFromOrderStatus(change.getFromOrderStatus()));
            }
        }
        // 写流水
        tcOrderOperateFlowRepository.batchCreate(flows);
        // 发消息
        orderMessageAbility.messageSend(change);
    }

    private List<TcOrderOperateFlowDO> getOpFlow(List<TcOrderDO> orders, OrderChangeOperate op) {
        List<TcOrderOperateFlowDO> list = new ArrayList<>();
        for (TcOrderDO order : orders) {
            if( order.getPrimaryOrderFlag() == 1 ) {
                TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
                flow.setOrderId(order.getOrderId());
                flow.setPrimaryOrderId(order.getPrimaryOrderId());
                flow.setToOrderStatus(order.getOrderStatus());
                flow.setOperator(String.valueOf(order.getCustId()));
                flow.setOperatorType(op.getOprType());
                flow.setOpName(OrderStatusEnum.codeOf(order.getPrimaryOrderStatus()).getInner());
                list.add(flow);
            }
        }
        return list;
    }

    private List<TcOrderOperateFlowDO> getOpFlow(MainOrder mainOrder, OrderChangeOperate op) {
        List<TcOrderOperateFlowDO> list = new ArrayList<>();
       /* for (SubOrder subOrder : mainOrder.getSubOrders()) {
            TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
            flow.setOrderId(subOrder.getOrderId());
            flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            flow.setToOrderStatus(subOrder.getOrderStatus());
            flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
            flow.setOperatorType(op.getOprType());
            flow.setOpName(op.getOpName());
            list.add(flow);
        }*/
        TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
        flow.setOrderId(mainOrder.getPrimaryOrderId());
        flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        flow.setToOrderStatus(mainOrder.getPrimaryOrderStatus());
        flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
        flow.setOperatorType(op.getOprType());
        flow.setOpName(OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()).getInner());
        list.add(flow);
        return list;
    }

    public static TcOrderOperateFlowDO buildOrderPay(MainOrder mainOrder,
                                                     OrderStatusEnum statusFrom,
                                                     OrderStatusEnum statusTo) {
        TcOrderOperateFlowDO tcOrderOperateFlowDO = new TcOrderOperateFlowDO();
        //支付都是按照主单维度、生命周期在支付环节只需记录主单变更
        tcOrderOperateFlowDO.setOrderId(mainOrder.getPrimaryOrderId());
        tcOrderOperateFlowDO.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        tcOrderOperateFlowDO.setOperator(mainOrder.getCustomer().getCustId().toString());
        tcOrderOperateFlowDO.setGmtModified(new Date());
        tcOrderOperateFlowDO.setFromOrderStatus(statusFrom.getCode());
        tcOrderOperateFlowDO.setToOrderStatus(statusTo.getCode());
        tcOrderOperateFlowDO.setGmtCreate(new Date());
        if(statusTo.getCode().equals(OrderStatusEnum.WAITING_FOR_PAYMENT.getCode())){
            OrderChangeOperate op = OrderChangeOperateEnum.CUST_PAY_COMFIRMING;
            tcOrderOperateFlowDO.setOperatorType(op.getOprType());
            tcOrderOperateFlowDO.setOpName(I18NMessageUtils.getMessage("waiting.payment"));
        }
        else if(statusTo.getCode().equals(OrderStatusEnum.PAYMENT_CONFIRMING.getCode())){
            OrderChangeOperate op = OrderChangeOperateEnum.CUST_PAY_COMFIRMING;
            tcOrderOperateFlowDO.setOperatorType(op.getOprType());
            tcOrderOperateFlowDO.setOpName(I18NMessageUtils.getMessage("payment.confirming"));
        }else if(statusTo.getCode().equals(OrderStatusEnum.PAYMENT_CONFIRMED.getCode())){
            OrderChangeOperate op = OrderChangeOperateEnum.CUST_PAY_COMFIRM;
            tcOrderOperateFlowDO.setOperatorType(op.getOprType());
            tcOrderOperateFlowDO.setOpName(I18NMessageUtils.getMessage("payment.confirmed"));
        }else if(statusTo.getCode().equals(OrderStatusEnum.COMPLETED.getCode())){
            OrderChangeOperate op = OrderChangeOperateEnum.COMPLETED;
            tcOrderOperateFlowDO.setOperatorType(op.getOprType());
            tcOrderOperateFlowDO.setOpName(I18NMessageUtils.getMessage("completed"));
        }

        return tcOrderOperateFlowDO;
    }

    public static void main(String[] args) {
        LocaleContextHolder.setLocale(new Locale("en"));
        System.out.println(I18NMessageUtils.getMessage("payment.confirming"));
    }

}
