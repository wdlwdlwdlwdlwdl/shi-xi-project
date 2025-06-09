package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryNoPageRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 说明： 取消原因配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "取消原因配置", tags = "取消原因配置")
public interface CancelReasonFacade {

    @ApiOperation("查询取消原因list")
    RpcResponse<PageInfo<CancelReasonDTO>> queryCancelReason(CancelReasonQueryRpcReq req);

    @ApiOperation("查询取消原因list")
    RpcResponse<List<CancelReasonDTO>> queryCancelReasonAll(CancelReasonQueryNoPageRpcReq req);

    @ApiOperation("保存取消原因")
    RpcResponse<CancelReasonDTO> saveCancelReason(CancelReasonRpcReq req);

    @ApiOperation("更新取消原因")
    RpcResponse<CancelReasonDTO> updateCancelReason(CancelReasonRpcReq req);

    @ApiOperation("查看取消原因")
    RpcResponse<CancelReasonDTO> cancelReasonDetail(CancelReasonRpcReq req);

    @ApiOperation("查看取消原因")
    RpcResponse<CancelReasonDTO> cancelReasonDetailByCode(CancelReasonRpcReq req);

}
