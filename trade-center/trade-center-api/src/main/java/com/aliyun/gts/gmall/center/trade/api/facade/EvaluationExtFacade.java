package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.IdRpcReq;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import io.swagger.annotations.Api;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:47
 **/
@Api("评论扩展接口")
public interface EvaluationExtFacade {

    RpcResponse<EvaluationDTO> getById(IdRpcReq idReq);

    RpcResponse<Boolean> update(EvaluationDTORpcReq evaluationDTO);

    RpcResponse<Boolean> updateEvaluation(EvaluationDTORpcReq evaluationDTO);
}
