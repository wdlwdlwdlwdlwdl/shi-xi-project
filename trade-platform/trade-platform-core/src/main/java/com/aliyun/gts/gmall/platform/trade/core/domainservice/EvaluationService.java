package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationExtendModifyReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;

import java.util.List;

public interface EvaluationService {

    void evaluate(List<EvaluationRpcReq> evaluationRpcReq);

    void evaluate(EvaluationRpcReq evaluationRpcReq);

    void additionalEvaluate(List<EvaluationRpcReq> evaluationRpcReq);

    void addExtraEvaluate(List<EvaluationRpcReq> evaluationRpcReq);

    void updateExtendInfo(EvaluationExtendModifyReq req);

    // 自动评价
    void autoEvaluation(Long primaryOrderId);
}
