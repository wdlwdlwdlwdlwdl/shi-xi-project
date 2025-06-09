package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class EvaluationRpcReqList extends TradeCommandRpcRequest {

    @NotNull
    @Valid
    @Size(min = 1 , max = 100)
    List<EvaluationRpcReq> reqList;

}
