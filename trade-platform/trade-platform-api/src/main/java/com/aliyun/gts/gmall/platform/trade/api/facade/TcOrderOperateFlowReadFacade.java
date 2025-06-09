package com.aliyun.gts.gmall.platform.trade.api.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("TcOrderOperateFlowReadFacade")
public interface TcOrderOperateFlowReadFacade {

    @ApiOperation("查询订单操作记录")
    RpcResponse<TcOrderOperateFlowDTO> query(CommonByIdQuery commonByIdQuery);

    @ApiOperation("查询订单操作记录")
    RpcResponse<List<TcOrderOperateFlowDTO>> queryList(TcOrderOperateFlowRpcReq query);
}
