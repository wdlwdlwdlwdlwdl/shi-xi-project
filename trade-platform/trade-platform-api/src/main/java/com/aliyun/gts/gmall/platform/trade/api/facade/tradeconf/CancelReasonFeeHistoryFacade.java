package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeHistoryDTO;
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
@Api(value = "取消原因费用配置记录", tags = "取消原因费用配置记录")
public interface CancelReasonFeeHistoryFacade {

    @ApiOperation("查询取消原因费用配置记录list")
    RpcResponse<List<CancelReasonFeeHistoryDTO>> queryCancelReasonFee(CancelReasonFeeHistoryRpcReq req);

    @ApiOperation("保存取消原因费用配置记录")
    RpcResponse<CancelReasonFeeHistoryDTO> saveCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req);
}
