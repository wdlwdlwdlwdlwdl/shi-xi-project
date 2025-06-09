package com.aliyun.gts.gmall.platform.trade.api.facade.evaluation;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationExtendModifyReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReqList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("评价")
public interface EvaluationWriteFacade {

    @ApiOperation("用户评价订单--批量接口")
    RpcResponse evaluateOrder(EvaluationRpcReqList reqList);

    @ApiOperation("用户评价订单--单条接口")
    RpcResponse evaluateOrder(EvaluationRpcReq req);

    @ApiOperation("用户追评订单")
    RpcResponse additionalEvaluateOrder(EvaluationRpcReqList reqList);

    @ApiOperation("通用添加评价记录")
    RpcResponse addExtraEvaluate(EvaluationRpcReqList reqList);

    @ApiOperation("更新评价扩展内容")
    RpcResponse updateExtendInfo(EvaluationExtendModifyReq req);
}
