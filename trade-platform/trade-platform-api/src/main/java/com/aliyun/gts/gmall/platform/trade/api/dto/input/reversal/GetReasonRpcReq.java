package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查询售后原因")
public class GetReasonRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty("售后类型 (仅退款, 退货退款), ReversalTypeEnum")
    private Integer reversalType;
}
