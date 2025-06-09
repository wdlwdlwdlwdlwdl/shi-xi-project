package com.aliyun.gts.gmall.platform.trade.api.facade;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsRpcReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("TcLogisticsWriteFacade")
public interface TcLogisticsWriteFacade {

    @ApiOperation("更新物流信息")
    RpcResponse  updateLogistics(LogisticsRpcReq req);

}
