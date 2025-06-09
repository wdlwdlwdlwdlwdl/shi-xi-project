package com.aliyun.gts.gmall.center.trade.api.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 卖家确认收款
 */
@Data
public class PaymentSellerReceivedRpcReq extends PaymentIdRpcReq{

    @ApiModelProperty("收款的流水id")
    String flowId;
    @ApiModelProperty("备注")
    String memo;
    @ApiModelProperty("是否收款成功 0否 1是")
    Integer received;
    @ApiModelProperty("收款单关闭原因 PayColseTypeEnum")
    Integer closeType;

}
