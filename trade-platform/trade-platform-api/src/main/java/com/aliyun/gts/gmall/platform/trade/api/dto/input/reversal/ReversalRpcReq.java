package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("售后统计查询")
public class ReversalRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty(value = "售后单创建时间范围")
    private DateRangeParam createTime;

    @ApiModelProperty(value = "订单创建时间范围")
    private DateRangeParam orderTime;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "主售后单id")
    private Long primaryReversalId;

    @ApiModelProperty("售后原因code")
    private Integer reversalReasonCode;
}
