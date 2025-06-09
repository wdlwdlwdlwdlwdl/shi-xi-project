package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
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
public interface CancelReasonHistoryFacade {

    @ApiOperation("查询取消原因记录list")
    RpcResponse<List<CancelReasonHistoryDTO>> queryCancelReason(CancelReasonHistoryRpcReq req);

    @ApiOperation("保存取消记录原因")
    RpcResponse<CancelReasonHistoryDTO> saveCancelReasonHistory(CancelReasonHistoryRpcReq req);
}
