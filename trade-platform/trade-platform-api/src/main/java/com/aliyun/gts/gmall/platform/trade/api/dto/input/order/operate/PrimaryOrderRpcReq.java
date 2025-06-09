package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("主订单 用于操作主订单")
@Data
public class PrimaryOrderRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty("主订单id")
    @NotNull
    Long primaryOrderId;

    @ApiModelProperty("买家id")
    Long custId;

    Long sellerId;

    int orderStatus;

    String remark;

    GenericEnum status;

    String reasonCode;

    String reasonName;

}
