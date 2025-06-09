package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QueryCommandRpcRequest extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "当前页", required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页数", required = true)
    private Integer pageSize;

}
