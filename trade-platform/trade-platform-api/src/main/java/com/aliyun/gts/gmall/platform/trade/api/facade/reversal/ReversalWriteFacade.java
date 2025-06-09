package com.aliyun.gts.gmall.platform.trade.api.facade.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("售后单写接口")
public interface ReversalWriteFacade {

    @ApiOperation("创建售后单, 返回售后主单ID")
    RpcResponse<Long> createReversal(CreateReversalRpcReq req);

    @ApiOperation("卖家同意售后")
    RpcResponse agreeBySeller(ReversalAgreeRpcReq req);

    @ApiOperation("卖家不同意售后,并关闭")
    RpcResponse refuseBySeller(ReversalRefuseRpcReq req);

    @ApiOperation("顾客取消售后")
    RpcResponse cancelByCustomer(ReversalModifyRpcReq req);

    @ApiOperation("顾客发货")
    RpcResponse sendDeliverByCustomer(ReversalDeliverRpcReq req);

    @ApiOperation("卖家确认收货")
    RpcResponse confirmDeliverBySeller(ReversalModifyRpcReq req);

    @ApiOperation("卖家拒绝收货,并关闭")
    RpcResponse refuseDeliverBySeller(ReversalRefuseRpcReq req);

    @ApiOperation("买家确认收到退款")
    RpcResponse buyerConfirmRefund(ReversalBuyerConfirmReq req);

    @ApiOperation("运营同意售后")
    RpcResponse agreeByOperation(ReversalAgreeRpcReq req);

    @ApiOperation("系统自动取消售后")
    RpcResponse cancelBySystem(ReversalModifyRpcReq req);
}
