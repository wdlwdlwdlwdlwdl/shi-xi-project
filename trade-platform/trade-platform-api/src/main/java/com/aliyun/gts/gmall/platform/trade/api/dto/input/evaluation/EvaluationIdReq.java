package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Data;

@Data
public class EvaluationIdReq extends AbstractQueryRpcRequest {

    private Long evaluationId;          // 主键, 必传
    private Long primaryOrderId;        // 分库分表键, 必传
    private Long custId;                // 非必填, 传入时仅用于数据owner校验
    private Long sellerId;              // 非必填, 传入时仅用于数据owner校验
    private Long orderId;
}
