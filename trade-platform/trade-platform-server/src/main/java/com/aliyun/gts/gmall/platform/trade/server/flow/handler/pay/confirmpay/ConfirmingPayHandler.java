package com.aliyun.gts.gmall.platform.trade.server.flow.handler.pay.confirmpay;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmingPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmingPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayOrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ConfirmingPayHandler implements ProcessFlowNodeHandler<ConfirmingPayCheckRpcReq, ConfirmingPayCheckRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private PayOrderStatusService payOrderStatusService;

    @Autowired
    private PayQueryService payQueryService;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private OrderTaskAbility orderTaskAbility;


    @Override
    public ConfirmingPayCheckRpcResp handleBiz(Map<String, Object> map, ConfirmingPayCheckRpcReq req) {
        log.info("支付确认中请求参数：",req);
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        //TODO验证支付ing状态
        orderStatusService.payComfirming(mainOrder);
        orderTaskAbility.orderTask(mainOrder, OrderStatusEnum.PAYMENT_CONFIRMING.getCode());
        ConfirmingPayCheckRpcResp resp = new ConfirmingPayCheckRpcResp();
        resp.setSuccess(true);
        log.info("支付确认中更新状态返回参数：",resp);
        return resp;
    }
}
