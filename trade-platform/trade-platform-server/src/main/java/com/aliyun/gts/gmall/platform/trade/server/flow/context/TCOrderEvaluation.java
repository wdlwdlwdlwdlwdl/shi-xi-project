package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcEvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.OrderEvaluation;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;

public class TCOrderEvaluation extends
    AbstractContextEntity<TcEvaluationRpcReq, OrderEvaluation, Void> {
}
