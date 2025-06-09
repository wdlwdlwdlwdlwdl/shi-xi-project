//package com.aliyun.gts.gmall.platform.trade.api.facade;
//
//import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
//import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
//
//import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//
///**
// * Created by auto-generated on 2021/03/23.
// */
//@Api("TcOrderOperateFlowWriteFacade")
//public interface TcOrderOperateFlowWriteFacade {
//
//
//    @ApiOperation("写入订单操作记录")
//    RpcResponse<TcOrderOperateFlowDTO> createTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq);
//
//    @ApiOperation("更新订单操作记录")
//    RpcResponse<TcOrderOperateFlowDTO> updateTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq);
//
//    @ApiOperation("删除订单操作记录")
//    RpcResponse<Boolean> deleteTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq);
//}