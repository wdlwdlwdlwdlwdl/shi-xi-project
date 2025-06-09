package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import lombok.Data;

@Data
public class EvaluationDetailsReq {
    private Long evaluationId;
    private Long primaryOrderId;
    private Long orderId;
    private Long custId;
}
