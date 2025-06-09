package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.QueryCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("订单批量查询")
public class OrderBatchQueryRpcReq extends QueryCommandRpcRequest {

    @ApiModelProperty(value = "主订单id", required = true)
    private List<Long> primaryOrderIds;

    @ApiModelProperty(value = "是否查extend, 如是true将查询订单所有extend, 推荐使用extend单独接口按需精确查")
    private boolean includeExtend = false;

    @ApiModelProperty(value = "是否查售后信息 (目前就返回售后子单状态)")
    private boolean includeReversalInfo = false;

}
