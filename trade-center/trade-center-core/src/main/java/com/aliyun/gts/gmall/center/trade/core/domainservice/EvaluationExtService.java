package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:39
 **/

public interface EvaluationExtService {

    EvaluationDTO getById(Long id);

    Boolean updateEvaluation(EvaluationDTORpcReq evaluationDTO);

    Boolean updateExtendByPrimaryOrderId(EvaluationDTORpcReq evaluationDTO);
}
