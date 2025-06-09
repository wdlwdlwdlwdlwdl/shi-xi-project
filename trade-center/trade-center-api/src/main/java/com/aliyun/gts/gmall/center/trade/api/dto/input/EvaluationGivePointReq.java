package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Data;

@Data
public class EvaluationGivePointReq extends AbstractQueryRpcRequest {
    private Long custId;
    private Long createId;
    private String creator;
    private Integer evaluationPointCount;
    private Integer invalidType;
    private Integer invalidYear;
    private Integer invalidMonth;
    private Long evaluationId;
}
