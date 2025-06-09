package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderMergePayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单创建 step 13
 *    金额支付
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Slf4j
@Component
public class CreateOrderNewPaymentHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;

    @Autowired
    private com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade orderPayNewWriteFacade;

    @Autowired
    TcOrderRepository tcOrderRepository;

    /**
     * 开关0 关闭，1 打开
     */
    @Value("${createOrder.pay.switch:1}")
    private Integer paySwitch;

    @Override
    @Transactional
    public void handle(TOrderCreate inbound) {

        if(Objects.isNull(paySwitch) || paySwitch.equals(0))
        {
            log.warn("the paySwitch is closed:{}", paySwitch);
            return;
        }
        log.info("the paySwitch is open:{}", paySwitch);


        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();

        if(StringUtils.isBlank(createOrderRpcReq.getCardId()))
        {
            log.warn("card is closed:{}", createOrderRpcReq.getCardId());
            return;
        }
        CreatingOrder creatingOrder = inbound.getDomain();

       if(CollectionUtils.isEmpty(creatingOrder.getMainOrders()))
       {
           inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
           return;
       }
       if(creatingOrder.getMainOrders().size()==1) {
           //一个订单支付
           singlePay(inbound,createOrderRpcReq,creatingOrder);
           return;

       }
       //多个订单处理
        OrderMergePayRpcReq req = buildTradeMergePayRequest(createOrderRpcReq, creatingOrder);
        RpcResponse<OrderMergePayRpcResp> payResponse = this.orderPayWriteFacade.toMergePay(req);
        if (!payResponse.isSuccess()) {
            log.error("toPay error rpcResponse=" + payResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
            return;
        }
        // 1 调用epay
        OrderMergePayRpcResp reps = payResponse.getData();
        tcOrderRepository.updateCartIdByPrimaryIds(
                req.getPrimaryOrderIds(),
                reps.getCartId());
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setAccountId(createOrderRpcReq.getAccountId());
        ePayTokenRpcReq.setInvoiceID(reps.getCartId());
        ePayTokenRpcReq.setAmount(creatingOrder.getOrderPrice().getOrderRealAmt());
        ePayTokenRpcReq.setCardId(createOrderRpcReq.getCardId());
        ePayTokenRpcReq.setScope("payment");
        ePayTokenRpcReq.setDescription("payment");
        ePayTokenRpcReq.setLanguage(createOrderRpcReq.getLang());
        ePayTokenRpcReq.setPaymentType("cardId");
        ePayTokenRpcReq.setCustId(String.valueOf(createOrderRpcReq.getCustId()));
        ePayTokenRpcReq.setToken(reps.getPayToken());
        // 请求和支付结果要更新到支付流水上
        com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp newOrderPayRpcResp = new com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp();
        BeanUtils.copyProperties(reps, newOrderPayRpcResp);
        RpcResponse<Boolean> rpcResponse = orderPayNewWriteFacade.doPay(ePayTokenRpcReq, newOrderPayRpcResp);
        if (!rpcResponse.isSuccess()) {
            log.error("merger pay error rpcResponse=" + rpcResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
        }
        if (Objects.isNull(rpcResponse.getData()) || !rpcResponse.getData()) {
            log.error("merger pay error rpcResponse is false" + rpcResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
        }
    }

    private void singlePay(TOrderCreate inbound, CreateOrderRpcReq createOrderRpcReq, CreatingOrder creatingOrder) {
        // 支付处理！！！
        MainOrder mainOrder = creatingOrder.getMainOrders().get(0);
        // 写流水
        OrderPayRpcReq orderPayRpcReq = new OrderPayRpcReq();
        orderPayRpcReq.setOrderChannel(createOrderRpcReq.getOrderChannel());
        orderPayRpcReq.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        orderPayRpcReq.setOrderType(String.valueOf(mainOrder.getOrderType()));
        orderPayRpcReq.setCustId(createOrderRpcReq.getCustId());
        orderPayRpcReq.setBuyerName(mainOrder.getCustomer().getCustName());
        orderPayRpcReq.setPayMethod(creatingOrder.getPayChannel());

        //多阶段价格不一样
        PayPrice payPrice = mainOrder.getCurrentPayInfo().getPayPrice();
        orderPayRpcReq.setRealPayFee(payPrice.getOrderRealAmt());
        orderPayRpcReq.setPointCount(payPrice.getPointCount());
        orderPayRpcReq.setTotalOrderFee(payPrice.getTotalAmt());
        orderPayRpcReq.setClientInfo(createOrderRpcReq.getClientInfo());
        orderPayRpcReq.setLang(createOrderRpcReq.getLang());
        orderPayRpcReq.setUuid(createOrderRpcReq.getUuid());
        orderPayRpcReq.setTenantId(createOrderRpcReq.getTenantId());
        orderPayRpcReq.setTraceId(createOrderRpcReq.getTraceId());
        Integer stepNo = createOrderRpcReq.getStepNo();
        if (stepNo == null || stepNo < 1) {
            stepNo = mainOrder.getOrderAttr().getCurrentStepNo();
        }
        orderPayRpcReq.setStepNo(stepNo);
        orderPayRpcReq.setPayChannel(creatingOrder.getPayChannel());
        RpcResponse<OrderPayRpcResp> payResponse = orderPayWriteFacade.toPay(orderPayRpcReq);
        log.info("payResponse=" + payResponse);
        if (!payResponse.isSuccess()) {
            log.error("toPay error rpcResponse=" + payResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
            return;
        }

        // 1 调用epay
        OrderPayRpcResp reps = payResponse.getData();

        tcOrderRepository.updateCartIdByPrimaryIds(
                List.of(mainOrder.getPrimaryOrderId()),
                reps.getCartId());

        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setAccountId(createOrderRpcReq.getAccountId());
        ePayTokenRpcReq.setInvoiceID(reps.getCartId());
        ePayTokenRpcReq.setAmount(orderPayRpcReq.getRealPayFee());
        ePayTokenRpcReq.setCardId(createOrderRpcReq.getCardId());
        ePayTokenRpcReq.setScope("payment");
        ePayTokenRpcReq.setDescription("payment");
        ePayTokenRpcReq.setLanguage(orderPayRpcReq.getLang());
        ePayTokenRpcReq.setPaymentType("cardId");
        ePayTokenRpcReq.setCustId(String.valueOf(orderPayRpcReq.getCustId()));
        ePayTokenRpcReq.setToken(reps.getPayToken());
        // 请求和支付结果要更新到支付流水上
        com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp newOrderPayRpcResp = new com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp();
        BeanUtils.copyProperties(reps, newOrderPayRpcResp);
        RpcResponse<Boolean> rpcResponse = orderPayNewWriteFacade.doPay(ePayTokenRpcReq, newOrderPayRpcResp);
        if (!rpcResponse.isSuccess()) {
            log.error("pay error rpcResponse=" + rpcResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
        }
        if (Objects.isNull(rpcResponse.getData()) || !rpcResponse.getData()) {
            log.error("pay error rpcResponse is false" + rpcResponse);
            inbound.setError(PayErrorCode.PAY_FAILED);
        }
    }


    public OrderMergePayRpcReq buildTradeMergePayRequest(CreateOrderRpcReq createOrderRpcReq,CreatingOrder creatingOrder) {
        OrderMergePayRpcReq orderPayRpcReq = new OrderMergePayRpcReq();
        List<Long> primaryOrderIds = creatingOrder.getMainOrders().stream().map(MainOrder::getPrimaryOrderId).collect(Collectors.toList());
        orderPayRpcReq.setPrimaryOrderIds(primaryOrderIds);
        orderPayRpcReq.setOrderChannel(createOrderRpcReq.getOrderChannel());
        // 支付处理！！！
        MainOrder mainOrder=creatingOrder.getMainOrders().get(0);
        orderPayRpcReq.setOrderType(String.valueOf(mainOrder.getOrderType()));
        orderPayRpcReq.setCustId(createOrderRpcReq.getCustId());
        orderPayRpcReq.setPayMethod(creatingOrder.getPayChannel());
        orderPayRpcReq.setRealPayFee(creatingOrder.getOrderPrice().getOrderRealAmt());
        orderPayRpcReq.setPointCount(creatingOrder.getOrderPrice().getPointCount());
        orderPayRpcReq.setTotalOrderFee(creatingOrder.getOrderPrice().getOrderTotalAmt());
        orderPayRpcReq.setClientInfo(createOrderRpcReq.getClientInfo());
        orderPayRpcReq.setLang(createOrderRpcReq.getLang());
        orderPayRpcReq.setUuid(createOrderRpcReq.getUuid());
        orderPayRpcReq.setTenantId(createOrderRpcReq.getTenantId());
        orderPayRpcReq.setTraceId(createOrderRpcReq.getTraceId());
        orderPayRpcReq.setPayChannel(creatingOrder.getPayChannel());
        orderPayRpcReq.setCustName(Objects.nonNull(creatingOrder.getCustomer())?creatingOrder.getCustomer().getCustName():null);
        return orderPayRpcReq;
    }


}
