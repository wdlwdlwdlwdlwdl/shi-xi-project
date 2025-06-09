//package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;
//
//import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.*;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.base.RefundKeyCommandRpcReq;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.ConfirmPayRpcResp;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderMergePayRpcResp;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderRefundRpcResp;
//import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade;
//import org.apache.dubbo.common.utils.ConcurrentHashSet;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Component
//public class MockOrderPayWriteFacade implements OrderPayWriteFacade {
//    static final Map<String, String> payId2OrdId = new ConcurrentHashMap<>();
//    static final Map<String, String> ordId2PayId = new ConcurrentHashMap<>();
//    static final Map<String, String> refundId2RevId = new ConcurrentHashMap<>();
//    static final Map<String, String> revId2RefundId = new ConcurrentHashMap<>();
//
//    private static final AtomicLong payIdGen = new AtomicLong(100L);
//    private static final AtomicLong refundIdGen = new AtomicLong(100L);
//
//    @Override
//    public RpcResponse<OrderPayRpcResp> toPay(OrderPayRpcReq orderPayRpcReq) {
//        String payId = String.valueOf(payIdGen.incrementAndGet());
//        OrderPayRpcResp resp = new OrderPayRpcResp();
//        resp.setPayData("XXX");
//        resp.setPayFlowId("XXX");
//        resp.setPayId(payId);
//        payId2OrdId.put(payId, orderPayRpcReq.getPrimaryOrderId());
//        ordId2PayId.put(orderPayRpcReq.getPrimaryOrderId(), payId);
//        return RpcResponse.ok(resp);
//    }
//
//    @Override
//    public RpcResponse<OrderMergePayRpcResp> toMergePay(OrderMergePayRpcReq orderMergePayRpcReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse<ConfirmPayRpcResp> confirmPay(ConfirmPayRpcReq confirmPayRpcReq) {
//        ConfirmPayRpcResp resp = new ConfirmPayRpcResp();
//        resp.setPaySuccess(true);
//        return RpcResponse.ok(resp);
//    }
//
//    @Override
//    public RpcResponse cancelPay(PayCancelRpcReq payCancelRpcReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse settle(PaySettleRpcReq paySettleRpcReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse<OrderRefundRpcResp> createRefund(OrderRefundRpcReq orderRefundRpcReq) {
//        String refundId = String.valueOf(refundIdGen.incrementAndGet());
//        OrderRefundRpcResp resp = new OrderRefundRpcResp();
//        resp.setRefundId(refundId);
//        refundId2RevId.put(refundId, orderRefundRpcReq.getPrimaryReversalId());
//        revId2RefundId.put(orderRefundRpcReq.getPrimaryReversalId(), refundId);
//        return RpcResponse.ok(resp);
//    }
//
//    @Override
//    public RpcResponse cancelRefund(RefundKeyCommandRpcReq refundKeyCommandRpcReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse updatePayBizExtend(PayExtendModifyRpcReq payExtendModifyRpcReq) {
//        return null;
//    }
//
//    @Override
//    public RpcResponse updateRefundBizExtend(RefundExtendModifyRpcReq refundExtendModifyRpcReq) {
//        return null;
//    }
//}
