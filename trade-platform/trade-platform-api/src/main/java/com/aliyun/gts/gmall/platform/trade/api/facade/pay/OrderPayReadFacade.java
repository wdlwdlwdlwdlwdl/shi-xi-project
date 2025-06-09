package com.aliyun.gts.gmall.platform.trade.api.facade.pay;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmingPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.PayRenderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmingPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author xinchen
 */
@Api("支付查询接口")
public interface OrderPayReadFacade {

    @ApiOperation("收银台展示")
    RpcResponse<PayRenderRpcResp> payRender(PayRenderRpcReq payRenderRpcReq);


    @ApiOperation("支付确认")
    RpcResponse<ConfirmPayCheckRpcResp> confirmPay(ConfirmPayCheckRpcReq confirmPayCheckRpcReq);

    @ApiOperation("获取payToken 信息")
    RpcResponse<String> getPayTokenInfo(String token);

    @ApiOperation("支付中")
    RpcResponse<ConfirmingPayCheckRpcResp> confirmingPay(ConfirmingPayCheckRpcReq confirmingPayCheckRpcReq);

}
