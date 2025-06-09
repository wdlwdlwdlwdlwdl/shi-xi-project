package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseReversalTest extends BaseTradeTest {
    protected static final long SELLER_ID = TestConstants.SELLER_ID;
    protected static final Long CUST_ID = TestConstants.CUST_ID;
    protected static final Long ITEM_ID = TestConstants.ITEM_ID;
    protected static final Long SKU_ID_1 = TestConstants.SKU_ID_1;
    protected static final Long SKU_ID_2 = TestConstants.SKU_ID_2;
    protected static final Long SKU_ID_3 = TestConstants.SKU_ID_3;
    protected static final Long SKU_ID_4 = TestConstants.SKU_ID_4;
    protected static final int REASON_CODE_1 = 101;
    protected static final int REASON_CODE_2 = 201;

    @Autowired
    protected ReversalReadFacade reversalReadFacade;
    @Autowired
    protected ReversalWriteFacade reversalWriteFacade;
    @Autowired
    protected ReversalQueryService reversalQueryService;
    @Autowired
    protected TcOrderRepository tcOrderRepository;
    @Autowired
    protected OrderReadFacade orderReadFacade;
    @Autowired
    protected OrderWriteFacade orderWriteFacade;
    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;


    protected ReversalSubOrderInfo newReversalSubOrderInfo(Long subOrderId, Integer itemNum, Long refundFee) {
        ReversalSubOrderInfo n = new ReversalSubOrderInfo();
        n.setOrderId(subOrderId);
        n.setCancelQty(itemNum);
        n.setCancelAmt(refundFee);
        return n;
    }

    protected static LogisticsInfoRpcReq newLogis(Integer companyType, String logisticsId) {
        LogisticsInfoRpcReq req = new LogisticsInfoRpcReq();
        req.setCompanyType(companyType);
        req.setLogisticsId(logisticsId);
        return req;
    }

    protected static ReceiverDTO getReceiverDTO() {
        ReceiverDTO r = new ReceiverDTO();
        r.setPhone("15000000000");
        r.setDeliveryAddr("省市区xxx");
        r.setReceiverName("aaa");
        return r;
    }

    protected MainReversal getReversal(Long primaryReversalId) {
        return reversalQueryService.queryReversal(primaryReversalId, null);
    }

    protected List<Long> createOrd(OrderStatusEnum orderStatus, long usePoint) {
        return createOrd(orderStatus, usePoint, 2);
    }

    protected List<Long> createOrd(OrderStatusEnum orderStatus, long usePoint, int subOrders) {
        return createOrd(orderStatus, OrderStageEnum.ON_SALE, usePoint, subOrders);
    }

    protected List<Long> createOrd(OrderStatusEnum orderStatus, OrderStageEnum stage, long usePoint, int subOrders) {
        // 确认订单
        String token;
        long realAmt;
        long totalFee;
        {
            List<ConfirmItemInfo> items = new ArrayList<>();
            if (subOrders >= 1) {items.add(getConfirmItem(ITEM_ID, SKU_ID_1, 3));}
            if (subOrders >= 2) {items.add(getConfirmItem(ITEM_ID, SKU_ID_2, 3));}
            if (subOrders >= 3) {items.add(getConfirmItem(ITEM_ID, SKU_ID_3, 3));}
            if (subOrders >= 4) {items.add(getConfirmItem(ITEM_ID, SKU_ID_4, 3));}

            ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
            req.setCustId(CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setOrderItems(items);
            req.setUsePointCount(usePoint);
            RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            token = resp.getData().getConfirmOrderToken();
            realAmt = resp.getData().getRealAmt();
            totalFee = resp.getData().getTotalAmt();
        }

        // 创建订单
        long primaryOrderId;
        String payChannel;
        int payType;
        {
            CreateOrderRpcReq req = new CreateOrderRpcReq();
            req.setClientInfo(getClientInfo());
            req.setConfirmOrderToken(token);
            req.setCustId(CUST_ID);
            if (realAmt == 0) {
                payChannel = "103";
                payType = PayTypeEnum.ASSERTS_PAY.getCode();
            } else {
                payChannel = "101";
                payType = PayTypeEnum.ONLINE_PAY.getCode();
            }
            req.setPayChannel(payChannel);
            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            primaryOrderId = resp.getData().getOrders().get(0).getPrimaryOrderId();
        }

        // 支付
        {
            OrderPayRpcReq req = new OrderPayRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setCustId(CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setPayChannel(payChannel);
            req.setTotalOrderFee(totalFee);
            req.setRealPayFee(realAmt);
            RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }

        // 改订单状态
        if (orderStatus == null) {
            orderStatus = OrderStatusEnum.ORDER_WAIT_DELIVERY;
        }
        tcOrderRepository.updateStatusAndStageByPrimaryId(
                primaryOrderId,
                orderStatus.getCode(),
                stage.getCode(),
                null);


        // 结果
        {
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
            List<Long> ids = list.stream()
                    .filter(ord -> ord.getPrimaryOrderFlag() == null || ord.getPrimaryOrderFlag().intValue() == 0)
                    .map(ord -> ord.getOrderId())
                    .collect(Collectors.toList());
            List<Long> result = new ArrayList<>();
            result.add(primaryOrderId);
            result.addAll(ids);
            return result;
        }
    }

    protected void confirmReceiveOrder(long primaryOrderId) {
        TcOrderDO order = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
        PrimaryOrderRpcReq req = new PrimaryOrderRpcReq();
        req.setPrimaryOrderId(primaryOrderId);
        req.setCustId(order.getCustId());
        RpcResponse resp = orderWriteFacade.confirmReceiveOrder(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    private ConfirmItemInfo getConfirmItem(Long itemId, Long skuId, Integer qty) {
        ConfirmItemInfo item = new ConfirmItemInfo();
        item.setItemId(itemId);
        item.setSkuId(skuId);
        item.setItemQty(qty);
        return item;
    }

    // 仅退款
    protected Long createRefundOnly(List<Long> order) {
        return createRefundOnly(order, false);
    }

    // 仅退款
    protected Long createRefundOnly(List<Long> order, boolean refundAll) {
        CreateReversalRpcReq req = new CreateReversalRpcReq();
        req.setCustId(CUST_ID);
        req.setPrimaryOrderId(order.get(0));
        req.setReversalType(ReversalTypeEnum.REFUND_ONLY.getCode());
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        req.setSubOrders(new ArrayList<>());

        long totalCancel = 0L;
        for (int i = 1; i < order.size(); i++) {
            req.getSubOrders().add(newReversalSubOrderInfo(order.get(i), null, refundAll ? null : 10L));
            totalCancel += 10;
        }
        req.setCancelAmt(refundAll ? null : totalCancel);

        req.setReversalReasonCode(REASON_CODE_1);
        req.setItemReceived(true);
        req.setCustMemo("仅退钱仅退钱仅退钱");
        req.setCustMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/i3/925431633/O1CN01vbXlbe1NvxNG5DNBJ_!!925431633-0-lubanu-s.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/i2/29/O1CN01X0uzap1C5K9JuIhUT_!!29-0-lubanu.jpg_430x430q90.jpg"));

        RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        return resp.getData();
    }

    // 退货退款
    protected Long createReturnItem(List<Long> order) {
        return createReturnItem(order, false);
    }

    // 退货退款
    protected Long createReturnItem(List<Long> order, boolean refundAll) {
        CreateReversalRpcReq req = new CreateReversalRpcReq();
        req.setCustId(CUST_ID);
        req.setPrimaryOrderId(order.get(0));
        req.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        req.setSubOrders(new ArrayList<>());

        long totalCancel = 0L;
        for (int i = 1; i < order.size(); i++) {
            req.getSubOrders().add(newReversalSubOrderInfo(order.get(i), null, refundAll ? null : 10L));
            totalCancel += 10;
        }
        req.setCancelAmt(refundAll ? null : totalCancel);

        req.setReversalReasonCode(REASON_CODE_2);
        req.setItemReceived(true);
        req.setCustMemo("退货退货退货退货退货");
        req.setCustMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/i3/925431633/O1CN01vbXlbe1NvxNG5DNBJ_!!925431633-0-lubanu-s.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/i2/29/O1CN01X0uzap1C5K9JuIhUT_!!29-0-lubanu.jpg_430x430q90.jpg"));


        RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        return resp.getData();
    }

    // 卖家同意
    protected void p01_sellerAgree(Long reversalId) {
        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setReceiver(getReceiverDTO());
        req.setSellerId(SELLER_ID);

        RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 卖家拒绝
    protected void p01_sellerRefuse(Long reversalId) {
        ReversalRefuseRpcReq req = new ReversalRefuseRpcReq();
        req.setSellerId(SELLER_ID);
        req.setPrimaryReversalId(reversalId);
        RpcResponse resp = reversalWriteFacade.refuseBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 顾客发货
    protected void p02_customerSend(Long reversalId) {
        ReversalDeliverRpcReq req = new ReversalDeliverRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setLogisticsList(Arrays.asList(newLogis(LogisticsCompanyTypeEnum.SF.getCode(), "11111")));
        req.setCustId(CUST_ID);

        RpcResponse resp = reversalWriteFacade.sendDeliverByCustomer(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 卖家收货
    protected void p03_sellerReceive(Long reversalId) {
        ReversalModifyRpcReq req = new ReversalModifyRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setSellerId(SELLER_ID);

        RpcResponse resp = reversalWriteFacade.confirmDeliverBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    // 卖家拒收
    protected void p03_sellerRefuseReceive(Long reversalId) {
        ReversalRefuseRpcReq req = new ReversalRefuseRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setSellerMemo("拒收拒收拒收");
        req.setSellerId(SELLER_ID);
        req.setSellerMedias(Arrays.asList(
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/2614298213/O1CN01KK9OwI2AXbY1M2C6i_!!2614298213.jpg_430x430q90.jpg",
                "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i3/2614298213/O1CN01zH5UsP2AXbXC7MYXL_!!2614298213.jpg_430x430q90.jpg"));

        RpcResponse resp = reversalWriteFacade.refuseDeliverBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void p0x_customerCancel(Long reversalId) {
        ReversalModifyRpcReq req = new ReversalModifyRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setCustId(CUST_ID);

        RpcResponse resp = reversalWriteFacade.cancelByCustomer(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void assertOrderStatus(TcOrderDO ord, OrderStatusEnum primaryStatus, OrderStatusEnum status) {
        Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), primaryStatus.getCode().intValue());
        Assert.assertEquals(ord.getOrderStatus().intValue(), status.getCode().intValue());
    }

    protected void assertOrderStage(TcOrderDO ord, OrderStageEnum stage) {
        Assert.assertEquals(ord.getOrderAttr().getOrderStage().intValue(), stage.getCode().intValue());
    }
}
