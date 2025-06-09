package com.aliyun.gts.gmall.platform.trade.api.facade.pay;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderMergePayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayV2RpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 下单支付请求等接口
 *
 * @author xinchen
 */
@Api("下单支付请求")
public interface OrderPayWriteFacade {

    /**
     * 发起支付
     *
     * @param orderPayRpcReq
     * @return
     */
    @ApiOperation("发起支付")
    RpcResponse<OrderPayRpcResp> toPay(OrderPayRpcReq orderPayRpcReq);

    /**
     * 发起合并支付
     *
     * @param orderMergePayRpcReq
     * @return
     */
    @ApiOperation("发起合并支付")
    RpcResponse<OrderMergePayRpcResp> toMergePay(OrderMergePayRpcReq orderMergePayRpcReq);

    /**
     * 支付
     * @param orderPayRpcReq
     * @param orderPayVO
     * @return
     */
    RpcResponse<Boolean> paymentV2(OrderPayV2RpcReq orderPayRpcReq, OrderPayRpcResp orderPayVO);

    /**
     * 支付失败 取消订单
     * @param primaryOrderIds
     * @return
     */
    @ApiOperation("取消订单")
    RpcResponse<Boolean> cancelOrders(List<Long> primaryOrderIds);

}
