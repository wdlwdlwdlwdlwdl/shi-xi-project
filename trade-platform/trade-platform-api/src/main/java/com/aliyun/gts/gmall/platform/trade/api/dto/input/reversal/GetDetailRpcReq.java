package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查询售后详情")
public class GetDetailRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty("售后主单ID")
    private Long primaryReversalId;

    @ApiModelProperty(value = "查询结果是否包含订单数据")
    private boolean includeOrderInfo = true;

    @ApiModelProperty("是否包含操作流水")
    private boolean includeFlows;

    @ApiModelProperty("是否包含售后原因内容")
    private boolean includeReason;
}
