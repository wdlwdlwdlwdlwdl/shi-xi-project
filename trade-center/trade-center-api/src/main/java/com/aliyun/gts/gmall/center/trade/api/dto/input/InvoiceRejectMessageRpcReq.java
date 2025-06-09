package com.aliyun.gts.gmall.center.trade.api.dto.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("发票退回--退票通知")
@Data
public class InvoiceRejectMessageRpcReq {
    @ApiModelProperty("税务发票平台对应生成的发票ID")
    private Long invoiceId;

    @ApiModelProperty("为确保信息安全，目前统一为'sys'")
    private String auditor;

    @ApiModelProperty("拒绝原因")
    private String rejectReason;

    @ApiModelProperty("对应上游系统的发票请求号")
    private String requestNo;

    @ApiModelProperty("时间戳")
    private Date timestamp;

}
