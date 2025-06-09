package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EvaluationBatchQueryRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty("批量查询条件")
    private List<EvaluationQueryRpcReq> batchConditions;
}
