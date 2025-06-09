package com.aliyun.gts.gmall.platform.trade.api.facade.reversal;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalCheckDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("售后单读接口")
public interface ReversalReadFacade {

    @ApiOperation("校验订单可申请售后数量")
    RpcResponse<ReversalCheckDTO> checkReversal(CheckReversalRpcReq req);

    @ApiOperation("获取售后原因")
    RpcResponse<List<ReversalReasonDTO>> queryReversalReasons(GetReasonRpcReq req);

    @ApiOperation("售后列表查询")
    RpcResponse<PageInfo<MainReversalDTO>> queryReversalList(ReversalQueryRpcReq req);

    @ApiOperation("售后详情查询")
    RpcResponse<MainReversalDTO> queryReversalDetail(GetDetailRpcReq req);

    @ApiOperation("查询退单商家")
    RpcResponse<List<MainReversalDTO>> queryRefundMerchant(ReversalRpcReq req);

    @ApiOperation("退单退单统计")
    RpcResponse<ReversalDTO> statisticsReversal(ReversalRpcReq req);
}
