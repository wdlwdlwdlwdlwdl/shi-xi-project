package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToPayData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToRefundData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

import java.util.List;
import java.util.Map;

public interface OrderPayRepository {
    String EXT_PAY_INFO = "__gm_extPayInfo";

    ToPayData toPay(MainOrder mainOrder);

    ToPayData toMergePay(List<MainOrder> mainOrders);

    boolean confirmPay(MainOrder mainOrder, String payFlowId);

    void cancelPay(MainOrder mainOrder, Integer stepNo);

    void settle(MainOrder mainOrder, Integer stepNo);

    ToRefundData createRefund(MainReversal mainReversal, Integer stepNo);

    OrderPay queryByUk(Long primaryOrderId, Long custId, Integer stepNo);

    List<PayChannel> queryAllPayChannel();

    void updatePayStatus(MainOrder mainOrder, String code);

    boolean confirmPayCartId(List<Map<String, String>> payFlowList, Long cartId);

    RpcResponse<Boolean> paymentV2(EPayTokenRpcReq ePayTokenRpcReq, OrderPayRpcResp newOrderPayRpcResp);

    ToRefundData createRefund(MainOrder mainOrder, Integer stepNo);
}
