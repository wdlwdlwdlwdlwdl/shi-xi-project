package com.aliyun.gts.gmall.platform.trade.api.facade.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationBatchQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationIdReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRateRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.*;
import io.swagger.annotations.Api;

import java.util.List;

@Api("评价查询")
public interface EvaluationReadFacade {

    /**
     * 评价查询
     */
    RpcResponse<PageInfo<ItemEvaluationDTO>> queryEvaluation(EvaluationQueryRpcReq req);

    /**
     * 评价分页查询
     *
     * @param req
     * @return
     */
    RpcResponse<PageInfo<ItemEvaluationV2DTO>> queryEvaluationV2(EvaluationQueryRpcReq req);

    /**
     * 评价条数查询
     */
    RpcResponse<Integer> queryEvaluationCount(EvaluationQueryRpcReq req);

    /**
     * 批量 评价条数查询
     */
    RpcResponse<List<Integer>> batchQueryEvaluationCount(EvaluationBatchQueryRpcReq req);

    /**
     * 通用主键查询
     */
    RpcResponse<EvaluationDTO> querySingleById(EvaluationIdReq req);

    /**
     * 通用主键查询
     */
    RpcResponse<List<EvaluationDTO>> getEvaluationWithReplies(EvaluationIdReq req);
    /**
     * 查询商户商品评价统计
     */
    RpcResponse<EvaluationRateDTO> rateStatistics(EvaluationRateRpcReq req);

    /**
     * 查询商品评价图片
     */
    RpcResponse<List<EvaluationRatePicDTO>> ratePicList(EvaluationRateRpcReq req);

    RpcResponse<List<EvaluationDTO>> getEvaluationList(EvaluationIdReq req);

}
