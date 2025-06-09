package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationExtendModifyReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReqList;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.EvaluationService;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EvaluationWriteFacadeImpl implements EvaluationWriteFacade {

    @Autowired
    EvaluationService evaluationService;

    @Override
    public RpcResponse evaluateOrder(EvaluationRpcReqList req) {
       evaluationService.evaluate(req.getReqList());
       return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse evaluateOrder(EvaluationRpcReq req) {
        evaluationService.evaluate(req);
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse additionalEvaluateOrder(EvaluationRpcReqList req) {
        evaluationService.additionalEvaluate(req.getReqList());
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse addExtraEvaluate(EvaluationRpcReqList req) {
        evaluationService.addExtraEvaluate(req.getReqList());
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse updateExtendInfo(EvaluationExtendModifyReq req) {
        try {
            evaluationService.updateExtendInfo(req);
        } catch (GmallException e) {
            log.warn("updateExtendInfo failed ", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        }
        return RpcResponse.ok(null);
    }
}
