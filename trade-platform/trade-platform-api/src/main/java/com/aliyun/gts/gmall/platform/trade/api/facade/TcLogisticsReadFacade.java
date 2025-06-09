package com.aliyun.gts.gmall.platform.trade.api.facade;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcLogisticsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("TcLogisticsReadFacade")
public interface TcLogisticsReadFacade {

    @ApiOperation("查询物流详情")
    RpcResponse<List<LogisticsDetailDTO>> queryLogisticsDetail(LogisticsDetailQueryRpcReq req);

    RpcResponse<TcLogisticsDTO> queryLogistics(LogisticsDetailQueryRpcReq req);
}
