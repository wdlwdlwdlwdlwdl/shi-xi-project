//package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;
//
//import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.EmptyRpcReq;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.PayFlowIdQueryRpcReq;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.PayQueryRpcReq;
//import com.aliyun.gts.gmall.platform.pay.api.dto.input.RefundQueryRpcReq;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderPayDTO;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderRefundDTO;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayChannelDTO;
//import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayFlowDTO;
//import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
//import com.aliyun.gts.gmall.platform.pay.api.enums.RefundStatusEnum;
//import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
//import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.TestConstants;
//import com.google.common.collect.Lists;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class MockOrderPayReadFacade implements OrderPayReadFacade {
//    @Override
//    public RpcResponse<OrderPayDTO> queryPayInfo(PayQueryRpcReq payQueryRpcReq) {
//        String payId;
//        String ordId;
//        if (StringUtils.isNotBlank(payQueryRpcReq.getPayId())) {
//            payId = payQueryRpcReq.getPayId();
//            ordId = MockOrderPayWriteFacade.payId2OrdId.get(payId);
//            if (ordId == null) {
//                return RpcResponse.ok(null);
//            }
//        } else if (StringUtils.isNotBlank(payQueryRpcReq.getPrimaryOrderId())) {
//            ordId = payQueryRpcReq.getPrimaryOrderId();
//            payId = MockOrderPayWriteFacade.ordId2PayId.get(ordId);
//            if (payId == null) {
//                return RpcResponse.ok(null);
//            }
//        } else {
//            return RpcResponse.ok(null);
//        }
//
//        OrderPayDTO pay = new OrderPayDTO();
//        pay.setPayId(payId);
//        pay.setPrimaryOrderId(ordId);
//        pay.setCustId(TestConstants.CUST_ID.toString());
//
//        PayFlowDTO payFlow = new PayFlowDTO();
//        pay.setPayFlows(Lists.newArrayList(payFlow));
//        pay.setPayStatus(PayStatusEnum.PAID.getCode());
//        payFlow.setPayChannel("XXX");
//        return RpcResponse.ok(pay);
//    }
//
//    @Override
//    public RpcResponse<OrderRefundDTO> queryRefundInfo(RefundQueryRpcReq refundQueryRpcReq) {
//        String refundId;
//        String revId;
//        if (StringUtils.isNotBlank(refundQueryRpcReq.getRefundId())) {
//            refundId = refundQueryRpcReq.getRefundId();
//            revId = MockOrderPayWriteFacade.refundId2RevId.get(refundId);
//            if (revId == null) {
//                return RpcResponse.ok(null);
//            }
//        } else if (StringUtils.isNotBlank(refundQueryRpcReq.getPrimaryReversalId())) {
//            revId = refundQueryRpcReq.getPrimaryReversalId();
//            refundId = MockOrderPayWriteFacade.revId2RefundId.get(revId);
//            if (refundId == null) {
//                return RpcResponse.ok(null);
//            }
//        } else {
//            return RpcResponse.ok(null);
//        }
//
//        OrderRefundDTO refund = new OrderRefundDTO();
//        refund.setRefundId(refundId);
//        refund.setPrimaryReversalId(revId);
//        refund.setRefundStatus(RefundStatusEnum.REFUND_SUCCESS.getCode());
//        return RpcResponse.ok(refund);
//    }
//
//    @Override
//    public RpcResponse<List<PayChannelDTO>> queryAllPayChannel(EmptyRpcReq emptyRpcReq) {
//        PayChannelDTO chl = new PayChannelDTO();
//        chl.setChannelCode("XXX");
//        return RpcResponse.ok(Lists.newArrayList(chl));
//    }
//
//    @Override
//    public RpcResponse<PayFlowDTO> queryPayFlowById(PayFlowIdQueryRpcReq payFlowIdQueryRpcReq) {
//        PayFlowDTO payFlow = new PayFlowDTO();
//        payFlow.setPayChannel("XXX");
//        payFlow.setPayFlowId(payFlowIdQueryRpcReq.getPayFlowId());
//        return RpcResponse.ok(payFlow);
//    }
//}
