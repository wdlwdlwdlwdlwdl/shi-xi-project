package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("订单发货")
public class OrderSendRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty("主订单id")
    @NotNull
    Long primaryOrderId;

    @ApiModelProperty("卖家id")
    Long sellerId;

    @ApiModelProperty("买家id")
    Long custId;

    @ApiModelProperty("物流方式")
    String deliveryType;

}
