package com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 说明： 取消原因费用配置
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 16:21
 */
@Api(value = "取消原因费用配置", tags = "取消原因费用配置")
public interface CancelReasonFeeFacade {

    @ApiOperation("查询取消原因费用list")
    RpcResponse<PageInfo<CancelReasonFeeDTO>> queryCancelReasonFee(CancelReasonFeeQueryRpcReq req);

    @ApiOperation("保存取消原因费用")
    RpcResponse<CancelReasonFeeDTO> saveCancelReasonFee(CancelReasonFeeRpcReq req);

    @ApiOperation("更新取消原因费用")
    RpcResponse<CancelReasonFeeDTO> updateCancelReasonFee(CancelReasonFeeRpcReq req);

    @ApiOperation("查看取消原因费用")
    RpcResponse<CancelReasonFeeDTO> cancelReasonFeeDetail(CancelReasonFeeRpcReq req);
}
