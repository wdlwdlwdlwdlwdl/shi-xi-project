package com.aliyun.gts.gmall.center.trade.api.dto.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("退票申请驳回 --退票结果")
@Data
public class InvoiceReturnRejectMessageRpcReq{
    @ApiModelProperty("税务发票平台对应生成的发票ID")
    private Long invoiceId;

    @ApiModelProperty("发票编码")
    private String invoiceCode;

    @ApiModelProperty("发票号")
    private String invoiceNo;

    @ApiModelProperty("为确保信息安全，目前统一为'sys'")
    private String auditor;

    @ApiModelProperty("审批时间")
    private Date auditorDate;

    @ApiModelProperty("退票是否成功")
    private Boolean isSuccess;

    @ApiModelProperty("拒绝原因")
    private String rejectReason;

    @ApiModelProperty("时间戳")
    private Date timestamp;
}
