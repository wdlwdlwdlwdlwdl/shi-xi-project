package com.aliyun.gts.gmall.platform.trade.server.flow.handler.pay.confirmpay;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ConfirmPayHandler implements ProcessFlowNodeHandler<ConfirmPayCheckRpcReq, ConfirmPayCheckRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private PayQueryService payQueryService;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Override
    public ConfirmPayCheckRpcResp handleBiz(Map<String, Object> map, ConfirmPayCheckRpcReq req) {
        log.info("支付完成请求参数：",req);
        ConfirmPayCheckRpcResp resp = new ConfirmPayCheckRpcResp();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if(OrderStatusEnum.PAYMENT_CONFIRMED.getCode().equals(mainOrder.getPrimaryOrderStatus())){
            resp.setPaySuccess(false);
            return resp;
        }
        boolean b = orderPayRepository.confirmPay(mainOrder, req.getPayFlowId());
        OrderPay orderPayTrade = payQueryService.queryByOrder(mainOrder);
        if (b) {
            orderStatusService.onPaySuccess(mainOrder, orderPayTrade);
        }
        MainOrder order = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        orderTaskAbility.orderTask(order, order.getPrimaryOrderStatus());
        resp.setPaySuccess(b);
        log.info("支付完成更新状态返回参数：",resp);
        return resp;
    }
}
