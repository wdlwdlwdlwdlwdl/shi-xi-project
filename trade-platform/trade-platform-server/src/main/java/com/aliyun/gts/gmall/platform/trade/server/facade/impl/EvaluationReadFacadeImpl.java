package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationBatchQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationIdReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRateRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.EvaluationQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EvaluationReadFacadeImpl implements EvaluationReadFacade {

    @Autowired
    private EvaluationQueryService evaluationQueryService;

    @Override
    public RpcResponse<PageInfo<ItemEvaluationDTO>> queryEvaluation(EvaluationQueryRpcReq req) {
        return RpcResponse.ok(evaluationQueryService.queryEvaluation(req));
    }

    @Override
    public RpcResponse<PageInfo<ItemEvaluationV2DTO>> queryEvaluationV2(EvaluationQueryRpcReq req) {
        return RpcResponse.ok(evaluationQueryService.queryEvaluationV2(req));
    }

    @Override
    public RpcResponse<Integer> queryEvaluationCount(EvaluationQueryRpcReq req) {
        return RpcResponse.ok(evaluationQueryService.queryEvaluationCount(req));
    }

    @Override
    public RpcResponse<List<Integer>> batchQueryEvaluationCount(EvaluationBatchQueryRpcReq req) {
        return RpcResponse.ok(evaluationQueryService.batchQueryEvaluationCount(req));
    }

    @Override
    public RpcResponse<EvaluationDTO> querySingleById(EvaluationIdReq req) {
        try {
            return RpcResponse.ok(evaluationQueryService.querySingleById(req));
        } catch (GmallException e) {
            log.warn("query evaluation failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
    }

    @Override
    public RpcResponse<List<EvaluationDTO>> getEvaluationWithReplies(EvaluationIdReq req) {
        try {
            return RpcResponse.ok(evaluationQueryService.getEvaluationWithReplies(req));
        } catch (GmallException e) {
            log.warn("query evaluation failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
    }

    @Override
    public RpcResponse<List<EvaluationDTO>> getEvaluationList(EvaluationIdReq req) {
        try {
            return RpcResponse.ok(evaluationQueryService.getEvaluationList(req));
        } catch (GmallException e) {
            log.warn("query evaluation failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
    }

    @Override
    public RpcResponse<EvaluationRateDTO> rateStatistics(EvaluationRateRpcReq req) {
        try {
            return RpcResponse.ok(evaluationQueryService.rateStatistics(req));
        } catch (GmallException e) {
            log.warn("query evaluation failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
    }

    @Override
    public RpcResponse<List<EvaluationRatePicDTO>> ratePicList(EvaluationRateRpcReq req) {
        try {
            return RpcResponse.ok(evaluationQueryService.ratePicList(req));
        } catch (GmallException e) {
            log.warn("query evaluation failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
    }
}
