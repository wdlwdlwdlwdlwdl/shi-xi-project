package com.aliyun.gts.gmall.test.trade.base;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.RefundQueryRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderRefundDTO;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.RefundFlowDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.pay.api.enums.RefundStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.SeparateRule;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.test.trade.base.message.BaseTestConsumer;
import com.aliyun.gts.gmall.test.trade.base.message.MockPayMessage;
import lombok.Builder;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseStepTest extends BaseTest {
    protected static long ASYNC_CHECK_TIMES = 10;
    protected static long ASYNC_CHECK_INTERVAL = 1000;

    @Autowired
    protected OrderWriteFacade orderWriteFacade;
    @Autowired
    protected OrderReadFacade orderReadFacade;
    @Autowired
    protected OrderQueryAbility orderQueryAbility;
    @Autowired
    protected OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    protected TcOrderRepository tcOrderRepository;
    @Autowired
    private ReversalReadFacade reversalReadFacade;
    @Autowired
    protected ReversalWriteFacade reversalWriteFacade;
    @Autowired
    protected ReversalQueryService reversalQueryService;
    @Autowired
    protected TcReversalRepository tcReversalRepository;
    @Autowired
    private PayQueryService payQueryService;
    @Autowired
    private OrderPayReadFacade orderPayReadFacade;
    @Autowired
    private MockPayMessage mockPayMessage;

    // ==================== 正向操作 ====================

    protected long createOrder(long itemId, long skuId, long usePointCount) {
        // 确认订单
        String token;
        long realAmt;
        {
            ConfirmItemInfo item = new ConfirmItemInfo();
            item.setItemId(itemId);
            item.setSkuId(skuId);
            item.setItemQty(1);

            ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setOrderItems(Arrays.asList(item));
            req.setUsePointCount(usePointCount);
            RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
            printPretty("confirmOrderInfo", resp);
            Assert.assertTrue(resp.isSuccess());
            token = resp.getData().getConfirmOrderToken();
            realAmt = resp.getData().getRealAmt();
        }

        // 创建订单
        {
            CreateOrderRpcReq req = new CreateOrderRpcReq();
            req.setClientInfo(getClientInfo());
            req.setConfirmOrderToken(token);
            req.setCustId(TestConstants.CUST_ID);
            if (realAmt == 0) {
                req.setPayChannel(PayChannelEnum.POINT_ASSERTS.getCode());
            } else {
                req.setPayChannel(PayChannelEnum.ALIPAY.getCode());
            }

            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            return resp.getData().getOrders().get(0).getPrimaryOrderId();
        }
    }

    protected String payOrder(long primaryOrderId) {
        PayChannelEnum payChannel;
        MainOrder mainOrder;
        {
            mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            long realAmt = mainOrder.getCurrentStepOrder().getPrice().getRealAmt();
            if (realAmt > 0) {
                payChannel = PayChannelEnum.ALIPAY;
            } else {
                payChannel = PayChannelEnum.POINT_ASSERTS;
            }
        }
        String payFlowId;
        {
            OrderPayRpcReq req = new OrderPayRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setPayChannel(payChannel.getCode());
            //req.setTotalOrderFee(realFee);
            //req.setRealPayFee(realFee);
            // req.setStepNo(stepNo);
            RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
            printPretty("toPay", resp);
            Assert.assertTrue(resp.isSuccess());
            payFlowId = resp.getData().getPayFlowId();
        }
        mockPayMessage.sendPaySuccess(mainOrder, mainOrder.orderAttr().getCurrentStepNo());
        sleep(3000);
        OrderPay orderPay = payQueryService.queryByOrder(mainOrder);
        return orderPay.getPayId();
    }

    protected void sellerHandle(long primaryOrderId, Map<String, String> formData, boolean modify) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        StepOrderHandleRpcReq req = new StepOrderHandleRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setSellerId(mainOrder.getSeller().getSellerId());
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setStepNo(mainOrder.getOrderAttr().getCurrentStepNo());
        req.setFormData(formData);
        req.setModify(modify);
        RpcResponse resp = orderWriteFacade.handleStepOrderBySeller(req);
        printPretty("handleStepOrderBySeller", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void customerConfirm(long primaryOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        StepOrderHandleRpcReq req = new StepOrderHandleRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setSellerId(mainOrder.getSeller().getSellerId());
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setStepNo(mainOrder.getOrderAttr().getCurrentStepNo());
        RpcResponse resp = orderWriteFacade.confirmStepOrderByCustomer(req);
        printPretty("confirmStepOrderByCustomer", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void sendDelivery(long primaryOrderId) {
        LogisticsInfoRpcReq logis = new LogisticsInfoRpcReq();
        logis.setLogisticsId("aaa");
        logis.setCompanyType(1);
        TcLogisticsRpcReq req = new TcLogisticsRpcReq();
        req.setCustId(TestConstants.CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        req.setSellerId(TestConstants.SELLER_ID);
        req.setInfoList(Arrays.asList(logis));
        req.setReceiverPhone("xx");
        req.setReceiverName("xx");
        req.setReceiverAddr("xx");
        req.setType(LogisticsTypeEnum.REALITY.getCode());
        RpcResponse resp = orderWriteFacade.sendOrder(req);
        printPretty("sendOrder", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void confirmReceive(long primaryOrderId) {
        PrimaryOrderRpcReq req = new PrimaryOrderRpcReq();
        req.setCustId(TestConstants.CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        RpcResponse resp = orderWriteFacade.confirmReceiveOrder(req);
        printPretty("confirmReceiveOrder", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // ==================== 逆向操作 ====================

    protected long createRefundOnly(long primaryOrderId, long amt) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        ReversalSubOrderInfo sub = new ReversalSubOrderInfo();
        sub.setOrderId(mainOrder.getSubOrders().get(0).getOrderId());
        sub.setCancelAmt(amt);

        CreateReversalRpcReq req = new CreateReversalRpcReq();
        req.setCustId(TestConstants.CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        req.setReversalType(ReversalTypeEnum.REFUND_ONLY.getCode());
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        req.setSubOrders(Arrays.asList(sub));

        req.setReversalReasonCode(101);
        req.setItemReceived(true);
        req.setCustMemo("仅退钱仅退钱仅退钱");
        req.setCustMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/i3/925431633/O1CN01vbXlbe1NvxNG5DNBJ_!!925431633-0-lubanu-s.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/i2/29/O1CN01X0uzap1C5K9JuIhUT_!!29-0-lubanu.jpg_430x430q90.jpg"));

        RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
        printPretty("createReversal", resp);
        Assert.assertTrue(resp.isSuccess());
        return resp.getData();
    }

    protected long createReturnItem(long primaryOrderId, long amt, int qty) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        ReversalSubOrderInfo sub = new ReversalSubOrderInfo();
        sub.setOrderId(mainOrder.getSubOrders().get(0).getOrderId());
        sub.setCancelQty(qty);
        sub.setCancelAmt(amt);

        CreateReversalRpcReq req = new CreateReversalRpcReq();
        req.setCustId(TestConstants.CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        req.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        req.setSubOrders(Arrays.asList(sub));

        req.setReversalReasonCode(201);
        req.setItemReceived(true);
        req.setCustMemo("退货退货退货退货退货");
        req.setCustMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/i3/925431633/O1CN01vbXlbe1NvxNG5DNBJ_!!925431633-0-lubanu-s.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/i2/29/O1CN01X0uzap1C5K9JuIhUT_!!29-0-lubanu.jpg_430x430q90.jpg"));

        RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
        printPretty("createReversal", resp);
        Assert.assertTrue(resp.isSuccess());
        return resp.getData();
    }

    // 卖家同意
    protected void reSellerAgree(long primaryReversalId) {
        ReceiverDTO rev = new ReceiverDTO();
        rev.setPhone("15000000000");
        rev.setDeliveryAddr("省市区xxx");
        rev.setReceiverName("aaa");

        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setReceiver(rev);
        req.setSellerId(TestConstants.SELLER_ID);

        RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        MainReversal mr = reversalQueryService.queryReversal(primaryReversalId, null);
        if (ReversalTypeEnum.REFUND_ONLY.getCode().equals(mr.getReversalType())) {
            sleep(3000);
            for (Integer step : StepOrderUtils.getSteps(mr)) {
                mockPayMessage.sendRefundSuccess(mr, step);
            }
            sleep(3000);
        }
    }

    // 卖家拒绝
    protected void reSellerRefuse(long primaryReversalId) {
        ReversalRefuseRpcReq req = new ReversalRefuseRpcReq();
        req.setSellerId(TestConstants.SELLER_ID);
        req.setPrimaryReversalId(primaryReversalId);
        RpcResponse resp = reversalWriteFacade.refuseBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 顾客发货
    protected void reCustomerSend(long primaryReversalId) {
        LogisticsInfoRpcReq lg = new LogisticsInfoRpcReq();
        lg.setCompanyType(LogisticsCompanyTypeEnum.SF.getCode());
        lg.setLogisticsId("11111");

        ReversalDeliverRpcReq req = new ReversalDeliverRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setLogisticsList(Arrays.asList(lg));
        req.setCustId(TestConstants.CUST_ID);

        RpcResponse resp = reversalWriteFacade.sendDeliverByCustomer(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 卖家收货
    protected void reSellerReceive(long primaryReversalId) {
        ReversalModifyRpcReq req = new ReversalModifyRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setSellerId(TestConstants.SELLER_ID);

        RpcResponse resp = reversalWriteFacade.confirmDeliverBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 卖家拒收
    protected void reSellerRefuseReceive(long primaryReversalId) {
        ReversalRefuseRpcReq req = new ReversalRefuseRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setSellerMemo("拒收拒收拒收");
        req.setSellerId(TestConstants.SELLER_ID);
        req.setSellerMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/2614298213/O1CN01KK9OwI2AXbY1M2C6i_!!2614298213.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2614298213/O1CN01zH5UsP2AXbXC7MYXL_!!2614298213.jpg_430x430q90.jpg"));

        RpcResponse resp = reversalWriteFacade.refuseDeliverBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // ==================== check ====================

    protected void checkStatus(long primaryOrderId, int stepNo, OrderStatusEnum orderStatus, StepOrderStatusEnum... stepStatus) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        Assert.assertNotNull(mainOrder);

        List<TcOrderDO> orders = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        Assert.assertEquals(2, orders.size());
        for (TcOrderDO order : orders) {
            Assert.assertEquals(orderStatus.getCode(), order.getOrderStatus());
            Assert.assertEquals(orderStatus.getCode(), order.getPrimaryOrderStatus());
        }

        Assert.assertEquals(stepNo, mainOrder.orderAttr().getCurrentStepNo().intValue());
        for (int i = 0; i < stepStatus.length; i++) {
            StepOrder step = mainOrder.getStepOrders().get(i);
            Assert.assertEquals(step.getStatus(), stepStatus[i].getCode());
        }
    }

    protected void checkRefundStatus(long primaryReversalId, ReversalStatusEnum reversalStatus,
                                     RefundStatusEnum refund1, RefundStatusEnum pointFlow1, RefundStatusEnum realFlow1,
                                     RefundStatusEnum refund2, RefundStatusEnum pointFlow2, RefundStatusEnum realFlow2) {
        List<TcReversalDO> reversalList = tcReversalRepository.queryByPrimaryReversalId(primaryReversalId);
        Assert.assertEquals(2, reversalList.size());
        for (TcReversalDO reversal : reversalList) {
            Assert.assertEquals(reversalStatus.getCode(), reversal.getReversalStatus());
        }

        MainReversal mainReversal = getReversal(primaryReversalId);
        OrderRefundDTO refundDTO1 = getRefund(mainReversal, 1);
        OrderRefundDTO refundDTO2 = getRefund(mainReversal, 2);

        if (refund1 != null) {
            OrderRefundDTO refund = refundDTO1;
            Assert.assertNotNull(refund);
            Assert.assertEquals(refund1.getCode(), refund.getRefundStatus());
            List<RefundFlowDTO> detailList = refund.getRefundFlows();
            Map<Integer, RefundFlowDTO> typeMap = new HashMap<>();
            for (RefundFlowDTO detail : detailList) {
                Assert.assertTrue(PayTypeEnum.ASSERTS_PAY.getCode().equals(detail.getPayType())
                        || PayTypeEnum.ONLINE_PAY.getCode().equals(detail.getPayType()));
                Assert.assertNull(typeMap.get(detail.getPayType()));
                typeMap.put(detail.getPayType(), detail);
            }
            if (pointFlow1 != null) {
                RefundFlowDTO detail = typeMap.get(PayTypeEnum.ASSERTS_PAY.getCode());
                Assert.assertNotNull(detail);
                Assert.assertEquals(pointFlow1.getCode(), detail.getRefundFlowStatus());
            } else {
                Assert.assertNull(typeMap.get(PayTypeEnum.ASSERTS_PAY.getCode()));
            }
            if (realFlow1 != null) {
                RefundFlowDTO detail = typeMap.get(PayTypeEnum.ONLINE_PAY.getCode());
                Assert.assertNotNull(detail);
                Assert.assertEquals(realFlow1.getCode(), detail.getRefundFlowStatus());
            } else {
                Assert.assertNull(typeMap.get(PayTypeEnum.ONLINE_PAY.getCode()));
            }
        } else {
            Assert.assertNull(refundDTO1);
        }
        if (refund2 != null) {
            OrderRefundDTO refund = refundDTO2;
            Assert.assertNotNull(refund);
            Assert.assertEquals(refund2.getCode(), refund.getRefundStatus());
            List<RefundFlowDTO> detailList = refund.getRefundFlows();
            Map<Integer, RefundFlowDTO> typeMap = new HashMap<>();
            for (RefundFlowDTO detail : detailList) {
                Assert.assertTrue(PayTypeEnum.ASSERTS_PAY.getCode().equals(detail.getPayType())
                        || PayTypeEnum.ONLINE_PAY.getCode().equals(detail.getPayType()));
                Assert.assertNull(typeMap.get(detail.getPayType()));
                typeMap.put(detail.getPayType(), detail);
            }
            if (pointFlow2 != null) {
                RefundFlowDTO detail = typeMap.get(PayTypeEnum.ASSERTS_PAY.getCode());
                Assert.assertNotNull(detail);
                Assert.assertEquals(pointFlow2.getCode(), detail.getRefundFlowStatus());
            } else {
                Assert.assertNull(typeMap.get(PayTypeEnum.ASSERTS_PAY.getCode()));
            }
            if (realFlow2 != null) {
                RefundFlowDTO detail = typeMap.get(PayTypeEnum.ONLINE_PAY.getCode());
                Assert.assertNotNull(detail);
                Assert.assertEquals(realFlow2.getCode(), detail.getRefundFlowStatus());
            } else {
                Assert.assertNull(typeMap.get(PayTypeEnum.ONLINE_PAY.getCode()));
            }
        } else {
            Assert.assertNull(refundDTO2);
        }
    }

    protected void checkStepFee(long primaryOrderId, CheckFee step1, CheckFee step2) {
        // 主订单
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        PointExchange ex = mainOrder.orderAttr().getPointExchange();
        Assert.assertNotNull(mainOrder);
        checkStepFee(mainOrder.getOrderPrice(), ex, step1, step2, false);

        // 子订单
        Assert.assertEquals(mainOrder.getSubOrders().size(), 1);
        SubOrder subOrder = mainOrder.getSubOrders().get(0);
        checkStepFee(subOrder.getOrderPrice(), ex, step1, step2, true);

        // 阶段单
        checkStepFee(mainOrder.getStepOrders().get(0).getPrice(), ex, step1);
        checkStepFee(mainOrder.getStepOrders().get(1).getPrice(), ex, step2);
    }

    private void checkStepFee(OrderPrice orderPrice, PointExchange ex, CheckFee step1, CheckFee step2, boolean hasStepFee) {
        CheckFee fee = CheckFee.add(step1, step2);

        // 总金额
        long pointCount = 0L;
        if (fee.pointAmt > 0) {
            pointCount = ex.exAmtToCount(fee.pointAmt);
            Assert.assertTrue(pointCount > 0);
        }
        Assert.assertEquals(fee.realAmt, orderPrice.getOrderRealAmt().longValue());
        Assert.assertEquals(fee.pointAmt, orderPrice.getPointAmt().longValue());
        Assert.assertEquals(pointCount, orderPrice.getPointCount().longValue());
        Assert.assertEquals(fee.adjustPromotionAmt, NumUtils.getNullZero(orderPrice.getAdjustPromotionAmt()));
        Assert.assertEquals(fee.adjustFreightAmt, NumUtils.getNullZero(orderPrice.getAdjustFreightAmt()));
        Assert.assertEquals(fee.adjustPointAmt, NumUtils.getNullZero(orderPrice.getAdjustPointAmt()));
        Assert.assertEquals(fee.adjustPointCount, NumUtils.getNullZero(orderPrice.getAdjustPointCount()));
        Assert.assertEquals(fee.adjustRealAmt, NumUtils.getNullZero(orderPrice.getAdjustRealAmt()));

        if (orderPrice.getConfirmPrice() != null) {
            // 确认订单金额
            Assert.assertEquals(fee.confirmSellerRealAmt + fee.confirmPlatformRealAmt, orderPrice.getConfirmPrice().getConfirmRealAmt().longValue());
            Assert.assertEquals(fee.confirmSellerPointAmt + fee.confirmPlatformPointAmt, orderPrice.getConfirmPrice().getConfirmPointAmt().longValue());

            // 确认订单分账金额
            Map<String, Long> sepRealMap = new HashMap<>(orderPrice.getConfirmPrice().getSeparateRealAmt());
            long sellerRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_SELLER));
            long platformRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_PLATFORM));
            Assert.assertEquals(sellerRealAmt, fee.confirmSellerRealAmt);
            Assert.assertEquals(platformRealAmt, fee.confirmPlatformRealAmt);
            Assert.assertTrue(sepRealMap.isEmpty());

            Map<String, Long> sepPointMap = new HashMap<>(orderPrice.getConfirmPrice().getSeparatePointAmt());
            long sellerPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_SELLER));
            long platformPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_PLATFORM));
            Assert.assertEquals(sellerPointAmt, fee.confirmSellerPointAmt);
            Assert.assertEquals(platformPointAmt, fee.confirmPlatformPointAmt);
            Assert.assertTrue(sepPointMap.isEmpty());


        } else {
            Assert.assertEquals(fee.confirmSellerRealAmt, 0L);
            Assert.assertEquals(fee.confirmSellerPointAmt, 0L);
            Assert.assertEquals(fee.confirmPlatformRealAmt, 0L);
            Assert.assertEquals(fee.confirmPlatformPointAmt, 0L);
        }

        if (hasStepFee) {
            Assert.assertEquals(2, orderPrice.getStepAmt().size());
            checkStepFee(orderPrice.getStepAmt().get(1), ex, step1);
            checkStepFee(orderPrice.getStepAmt().get(2), ex, step2);
        } else {
            Assert.assertNull(orderPrice.getStepAmt());
        }
    }

    private void checkStepFee(StepOrderPrice orderPrice, PointExchange ex, CheckFee fee) {

        // 总金额
        long pointCount = 0L;
        if (fee.pointAmt > 0) {
            pointCount = ex.exAmtToCount(fee.pointAmt);
            Assert.assertTrue(pointCount > 0);
        }
        Assert.assertEquals(fee.realAmt, orderPrice.getRealAmt().longValue());
        Assert.assertEquals(fee.pointAmt, orderPrice.getPointAmt().longValue());
        Assert.assertEquals(pointCount, orderPrice.getPointCount().longValue());
        Assert.assertEquals(fee.adjustRealAmt, NumUtils.getNullZero(orderPrice.getAdjustRealAmt()));
        Assert.assertEquals(fee.adjustPointAmt, NumUtils.getNullZero(orderPrice.getAdjustPointAmt()));
        Assert.assertEquals(fee.adjustPointCount, NumUtils.getNullZero(orderPrice.getAdjustPointCount()));

        if (orderPrice.getConfirmPrice() != null) {
            // 确认订单金额
            Assert.assertEquals(fee.confirmSellerRealAmt + fee.confirmPlatformRealAmt, orderPrice.getConfirmPrice().getConfirmRealAmt().longValue());
            Assert.assertEquals(fee.confirmSellerPointAmt + fee.confirmPlatformPointAmt, orderPrice.getConfirmPrice().getConfirmPointAmt().longValue());

            // 确认订单分账金额
            Map<String, Long> sepRealMap = new HashMap<>(orderPrice.getConfirmPrice().getSeparateRealAmt());
            long sellerRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_SELLER));
            long platformRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_PLATFORM));
            Assert.assertEquals(sellerRealAmt, fee.confirmSellerRealAmt);
            Assert.assertEquals(platformRealAmt, fee.confirmPlatformRealAmt);
            Assert.assertTrue(sepRealMap.isEmpty());

            Map<String, Long> sepPointMap = new HashMap<>(orderPrice.getConfirmPrice().getSeparatePointAmt());
            long sellerPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_SELLER));
            long platformPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_PLATFORM));
            Assert.assertEquals(sellerPointAmt, fee.confirmSellerPointAmt);
            Assert.assertEquals(platformPointAmt, fee.confirmPlatformPointAmt);
            Assert.assertTrue(sepPointMap.isEmpty());


        } else {
            Assert.assertEquals(fee.confirmSellerRealAmt, 0L);
            Assert.assertEquals(fee.confirmSellerPointAmt, 0L);
            Assert.assertEquals(fee.confirmPlatformRealAmt, 0L);
            Assert.assertEquals(fee.confirmPlatformPointAmt, 0L);
        }
    }

    protected void checkReversalFee(long primaryReversalId, CheckFee step1, CheckFee step2) {
        checkReversalFee(primaryReversalId, step1, step2, true);
    }

    protected void checkReversalFee(long primaryReversalId, CheckFee step1, CheckFee step2, boolean checkRefund) {
        MainReversal mainReversal = getReversal(primaryReversalId);
        MainReversal reversal = reversalQueryService.queryReversal(primaryReversalId,
                ReversalDetailOption.builder().includeOrderInfo(true).build());

        PointExchange ex = reversal.getMainOrder().orderAttr().getPointExchange();
        checkStepFee(reversal.reversalFeatures(), ex, CheckFee.add(step1, step2), null);

        OrderRefundDTO refundDTO1 = null;
        OrderRefundDTO refundDTO2 = null;
        if (checkRefund) {
            refundDTO1 = getRefund(mainReversal, 1);
            refundDTO2 = getRefund(mainReversal, 2);
            Assert.assertEquals(step1.isZero(), refundDTO1 == null);
            Assert.assertEquals(step2.isZero(), refundDTO2 == null);
        }

        Map<Integer, RefundFee> map = new HashMap<>(reversal.reversalFeatures().getStepRefundFee());
        checkStepFee(map.remove(1), ex, step1, refundDTO1);
        checkStepFee(map.remove(2), ex, step2, refundDTO2);
        Assert.assertTrue(map.isEmpty());
    }

    protected void checkRemainFee(long primaryOrderId, long amt, int qty) {
        CheckReversalRpcReq req = new CheckReversalRpcReq();
        req.setCustId(TestConstants.CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        RpcResponse<List<ReversalSubOrderDTO>> resp = reversalReadFacade.checkReversal(req);
        printPretty("checkReversal", resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertEquals(1, resp.getData().size());

        ReversalSubOrderDTO subOrder = resp.getData().get(0);
        Assert.assertEquals(amt, subOrder.getMaxCancelAmt().longValue());
        Assert.assertEquals(qty, subOrder.getMaxCancelQty().intValue());
    }

    private void checkStepFee(RefundFee refundFee, PointExchange ex, CheckFee fee, OrderRefundDTO refund) {
        if (refundFee == null) {
            fee.assertZero();
            return;
        }
        long pointCount = 0L;
        if (fee.pointAmt > 0) {
            pointCount = ex.exAmtToCount(fee.pointAmt);
            Assert.assertTrue(pointCount > 0);
        }
        Assert.assertEquals(fee.realAmt, refundFee.getCancelRealAmt().longValue());
        Assert.assertEquals(fee.pointAmt, refundFee.getCancelPointAmt().longValue());
        Assert.assertEquals(pointCount, refundFee.getCancelPointCount().longValue());

        // 分账金额
        Map<String, Long> sepRealMap = new HashMap<>(refundFee.getCancelSeparateRealAmt());
        long sellerRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_SELLER));
        long platformRealAmt = NumUtils.getNullZero(sepRealMap.remove(SeparateRule.ROLE_PLATFORM));
        Assert.assertEquals(sellerRealAmt, fee.confirmSellerRealAmt);
        Assert.assertEquals(platformRealAmt, fee.confirmPlatformRealAmt);
        Assert.assertTrue(sepRealMap.isEmpty());

        Map<String, Long> sepPointMap = new HashMap<>(refundFee.getCancelSeparatePointAmt());
        long sellerPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_SELLER));
        long platformPointAmt = NumUtils.getNullZero(sepPointMap.remove(SeparateRule.ROLE_PLATFORM));
        Assert.assertEquals(sellerPointAmt, fee.confirmSellerPointAmt);
        Assert.assertEquals(platformPointAmt, fee.confirmPlatformPointAmt);
        Assert.assertTrue(sepPointMap.isEmpty());

        //refund 对象
        if (refund != null) {
            Assert.assertEquals(fee.realAmt, refund.getRefundAmt().longValue());

            //refundDetail
            List<RefundFlowDTO> details = refund.getRefundFlows();
            RefundFlowDTO pointDetail = null, realDetail = null;
            for (RefundFlowDTO detail : details) {
                if (PayTypeEnum.ASSERTS_PAY.getCode().equals(detail.getPayType().intValue())
                        && pointDetail == null) {
                    pointDetail = detail;
                } else if (PayTypeEnum.ONLINE_PAY.getCode().equals(detail.getPayType().intValue())
                        && realDetail == null) {
                    realDetail = detail;
                } else {
                    Assert.fail();
                }
            }
            Assert.assertEquals(pointCount > 0, pointDetail != null);
            Assert.assertEquals(fee.realAmt > 0, realDetail != null);
            if (pointDetail != null) {
                Assert.assertEquals(pointCount, NumUtils.getNullZero(pointDetail.getRefundQuantity()));
                Assert.assertEquals(0L, NumUtils.getNullZero(pointDetail.getRefundAmt()));
            }
            if (realDetail != null) {
                Assert.assertEquals(fee.realAmt, NumUtils.getNullZero(realDetail.getRefundAmt()));
            }
        }
    }

    protected void waitAsync(long primaryOrderId, OrderStatusEnum originStatus) {
        for (int i = 0; i < ASYNC_CHECK_TIMES; i++) {
            try {
                Thread.sleep(ASYNC_CHECK_INTERVAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (orderQueryAbility.getMainOrder(primaryOrderId)
                    .getPrimaryOrderStatus().intValue() != originStatus.getCode().intValue()) {
                return;
            }
        }
        Assert.fail("wait timeout");
    }

    protected void waitAsync() {
        try {
            Thread.sleep(ASYNC_CHECK_INTERVAL * 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setWaitAll() {
        ASYNC_CHECK_TIMES = 99999;
        BaseTestConsumer.WAIT_TIMES = 99999;
    }

    @Builder
    protected static class CheckFee {
        protected long realAmt;
        protected long pointAmt;

        protected long confirmSellerRealAmt;
        protected long confirmSellerPointAmt;
        protected long confirmPlatformRealAmt;
        protected long confirmPlatformPointAmt;

        protected long adjustPromotionAmt;
        protected long adjustFreightAmt;
        protected long adjustPointAmt;
        protected long adjustRealAmt;
        protected long adjustPointCount;

        public static CheckFee add(CheckFee f1, CheckFee f2) {
            return CheckFee.builder()
                    .realAmt(f1.realAmt + f2.realAmt)
                    .pointAmt(f1.pointAmt + f2.pointAmt)
                    .confirmSellerRealAmt(f1.confirmSellerRealAmt + f2.confirmSellerRealAmt)
                    .confirmSellerPointAmt(f1.confirmSellerPointAmt + f2.confirmSellerPointAmt)
                    .confirmPlatformRealAmt(f1.confirmPlatformRealAmt + f2.confirmPlatformRealAmt)
                    .confirmPlatformPointAmt(f1.confirmPlatformPointAmt + f2.confirmPlatformPointAmt)
                    .adjustPromotionAmt(f1.adjustPromotionAmt + f2.adjustPromotionAmt)
                    .adjustFreightAmt(f1.adjustFreightAmt + f2.adjustFreightAmt)
                    .adjustPointAmt(f1.adjustPointAmt + f2.adjustPointAmt)
                    .adjustRealAmt(f1.adjustRealAmt + f2.adjustRealAmt)
                    .adjustPointCount(f1.adjustPointCount + f2.adjustPointCount)
                    .build();
        }

        public void assertZero() {
            Assert.assertEquals(0, pointAmt);
            Assert.assertEquals(0, realAmt);
            Assert.assertEquals(0, confirmSellerRealAmt);
            Assert.assertEquals(0, confirmSellerPointAmt);
            Assert.assertEquals(0, confirmPlatformRealAmt);
            Assert.assertEquals(0, confirmPlatformPointAmt);
            Assert.assertEquals(0, adjustPromotionAmt);
            Assert.assertEquals(0, adjustFreightAmt);
            Assert.assertEquals(0, adjustPointAmt);
            Assert.assertEquals(0, adjustRealAmt);
            Assert.assertEquals(0, adjustPointCount);
        }

        public boolean isZero() {
            return pointAmt == 0
                    && realAmt == 0
                    && confirmSellerRealAmt == 0
                    && confirmSellerPointAmt == 0
                    && confirmPlatformRealAmt == 0
                    && confirmPlatformPointAmt == 0
                    && adjustPromotionAmt == 0
                    && adjustFreightAmt == 0
                    && adjustPointAmt == 0
                    && adjustRealAmt == 0
                    && adjustPointCount == 0;
        }
    }

    protected MainReversal getReversal(Long primaryReversalId) {
        return reversalQueryService.queryReversal(primaryReversalId, null);
    }

    protected OrderRefundDTO getRefund(MainReversal reversal, Integer stepNo) {
        RefundQueryRpcReq req = new RefundQueryRpcReq();
        req.setPrimaryReversalId(reversal.getPrimaryReversalId().toString());
        req.setCustId(reversal.getCustId().toString());
        req.setStepNo(stepNo);
        req.setIncludeRefundFlows(true);
        RpcResponse<OrderRefundDTO> resp = orderPayReadFacade.queryRefundInfo(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        return resp.getData();
    }
}
