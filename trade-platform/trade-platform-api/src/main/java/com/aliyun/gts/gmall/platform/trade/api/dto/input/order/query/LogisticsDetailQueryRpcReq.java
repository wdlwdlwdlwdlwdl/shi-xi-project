package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.QueryCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("物流详情查询")
public class LogisticsDetailQueryRpcReq extends QueryCommandRpcRequest {

    @ApiModelProperty(value = "主订单id", required = true)
    @NotNull
    private Long primaryOrderId;

    @ApiModelProperty(value = "主售后单id, 查退货物流使用")
    private Long primaryReversalId;

    @ApiModelProperty(value = "卖家id")
    private Long sellerId;

    @ApiModelProperty(value = "买家id")
    private Long custId;

}
