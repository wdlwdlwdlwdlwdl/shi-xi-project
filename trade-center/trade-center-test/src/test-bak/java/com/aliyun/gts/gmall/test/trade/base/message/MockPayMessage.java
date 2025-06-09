package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.RefundQueryRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.inner.PayFlowMessage;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderRefundDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.enums.RefundStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OversaleMessageDTO;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 收不到dev环境的消息，所以本地测试mock发送下消息
 */
@Service
public class MockPayMessage {

    @Autowired
    private PayQueryService payQueryService;
    @Autowired
    private MessageSendManager messageSendManager;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private OrderPayReadFacade orderPayReadFacade;

    @Value("${trade.pay.success.topic}")
    private String paySuccessTopic;
    @Value("${trade.pay.success.tag}")
    private String paySuccessTag;
    @Value("${trade.order.oversale.topic:GMALL_TRADE_ORDER_OVERSALE}")
    private String overSaleTopic;

    @Value("${trade.pay.refund.topic}")
    private String refundSuccessTopic;
    @Value("${trade.pay.refund.tag}")
    private String refundSuccessTag;

    public void sendPaySuccess(MainOrder mainOrder, Integer stepNo) {
        sendPaySuccess(mainOrder.getPrimaryOrderId(),
                mainOrder.getCustomer().getCustId(), stepNo);
    }

    public void sendPaySuccess(Long primaryOrderId, Long custId, Integer stepNo) {
        OrderPay orderPay = payQueryService.queryByOrder(primaryOrderId, custId, stepNo);
        Assert.assertNotNull(orderPay);
        Assert.assertEquals(PayStatusEnum.PAID.getCode(), orderPay.getPayStatus());

        PayFlowMessage flow = new PayFlowMessage();
        flow.setPayChannel(orderPay.getPayChannel());
        flow.setPayType(orderPay.getPayType());
        PaySuccessMessage msg = new PaySuccessMessage();
        msg.setPayId(orderPay.getPayId());
        msg.setPrimaryOrderId(orderPay.getPrimaryOrderId());
        msg.setCustId(orderPay.getCustId());
        msg.setStepNo(orderPay.getStepNo());
        msg.setPayTime(orderPay.getPayTime());
        msg.setPayFlows(Arrays.asList(flow));

        messageSendManager.sendMessage(msg, paySuccessTopic, paySuccessTag);
    }

    public void sendOverSale(Long primaryOrderId) {
        OversaleMessageDTO msg = new OversaleMessageDTO(primaryOrderId, false);
        messageSendManager.sendMessage(msg, overSaleTopic, "OVER_SALE");
    }

    public void sendRefundSuccess(Long primaryReversalId, Integer stepNo) {
        MainReversal mainReversal = reversalQueryService.queryReversal(primaryReversalId, null);
        sendRefundSuccess(mainReversal, stepNo);
    }

    public void sendRefundSuccess(MainReversal mainReversal, Integer stepNo) {
        OrderPay orderPay = payQueryService.queryByOrder(
                mainReversal.getMainOrder().getPrimaryOrderId(),
                mainReversal.getCustId(), stepNo);
        Assert.assertNotNull(orderPay);

        RefundQueryRpcReq req = new RefundQueryRpcReq();
        req.setPrimaryReversalId(mainReversal.getPrimaryReversalId().toString());
        req.setCustId(mainReversal.getCustId().toString());
        req.setStepNo(stepNo);
        RpcResponse<OrderRefundDTO> resp = orderPayReadFacade.queryRefundInfo(req);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertNotNull(resp.getData());
        Assert.assertEquals(RefundStatusEnum.REFUND_SUCCESS.getCode(),
                resp.getData().getRefundStatus());

        RefundSuccessMessage msg = new RefundSuccessMessage();
        msg.setPayId(orderPay.getPayId());
        msg.setPrimaryOrderId(orderPay.getPrimaryOrderId());
        msg.setCustId(orderPay.getCustId());
        msg.setStepNo(orderPay.getStepNo());
        msg.setPrimaryReversalId(mainReversal.getPrimaryReversalId().toString());
        msg.setRefundId(resp.getData().getRefundId());
        messageSendManager.sendMessage(msg, refundSuccessTopic, refundSuccessTag);
    }
}
