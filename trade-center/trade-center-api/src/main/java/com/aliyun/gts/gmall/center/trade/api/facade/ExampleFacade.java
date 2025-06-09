package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.ExampleRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ExampleDTO;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("新增接口例子")
public interface ExampleFacade {

    @ApiOperation("例子")
    RpcResponse<ExampleDTO> call(ExampleRpcReq req);
}
