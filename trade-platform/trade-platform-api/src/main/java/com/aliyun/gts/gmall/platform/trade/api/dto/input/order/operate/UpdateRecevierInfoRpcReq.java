package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel("修改订单收货人信息")
public class UpdateRecevierInfoRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty("主订单id")
    @NotNull
    Long primaryOrderId;

    @ApiModelProperty("卖家id")
    Long sellerId;

    @ApiModelProperty("买家id")
    Long custId;

    @ApiModelProperty("新收货信息id")
    @NotNull
    Long receiverInfoId;

}
