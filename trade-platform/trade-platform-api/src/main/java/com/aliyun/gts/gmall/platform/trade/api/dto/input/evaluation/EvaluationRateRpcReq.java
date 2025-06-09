package com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("评价统计")
@Data
public class EvaluationRateRpcReq extends TradeCommandRpcRequest {


    @ApiModelProperty("主订单id")
    Long primaryOrderId;

    @ApiModelProperty("子订单id")
    Long orderId;

    @ApiModelProperty("商品id")
    Long itemId;

    Long skuId;

    @ApiModelProperty("买家id")
    Long custId;

    @ApiModelProperty("卖家id")
    Long sellerId;

}
