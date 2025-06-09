package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import java.util.Date;

import com.aliyun.gts.gmall.manager.front.trade.constants.CATPayStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayQueryReq extends CommonPageReq {

    @ApiModelProperty("订单或支付单id")
    String paymentORorderId;
    @ApiModelProperty("支付单id")
    String paymentId;
    @ApiModelProperty("主订单id")
    Long primaryOrderId;
    @ApiModelProperty("买家名称")
    String custName;
    @ApiModelProperty("支付单状态")
    CATPayStatusEnum paymentStatus;
    @ApiModelProperty("创建时间start")
    Date gmtCreateStart;
    @ApiModelProperty("创建时间end")
    Date gmtCreateEnd;
    @ApiModelProperty("支付方式")
    String payChannel;
    @ApiModelProperty("卖家id")
    Long sellerId;

}
