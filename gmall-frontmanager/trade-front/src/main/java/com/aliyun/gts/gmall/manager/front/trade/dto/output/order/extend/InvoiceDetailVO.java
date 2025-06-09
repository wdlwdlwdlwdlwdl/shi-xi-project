package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class InvoiceDetailVO {
    private Long id;

    @ApiModelProperty("请求号")
    private String requestNo;

    @ApiModelProperty("主订单id")
    private Long primaryOrderId;

    @ApiModelProperty("销售类型")
    private Integer saleType;

    @ApiModelProperty("销售类型")
    private String saleTypeName;

    @ApiModelProperty("主单金额")
    private String masterOrderAmount;

    @ApiModelProperty("发票类型：0普通发票，1专票")
    private Integer invoiceType;

    @ApiModelProperty("发票类型：0普通发票，1专票")
    private String invoiceTypeName;

    @ApiModelProperty("发票代码")
    private String invoiceCode;

    @ApiModelProperty("发票号码")
    private String invoiceNo;

    @ApiModelProperty("开票金额")
    private String invoiceAmount;

    @ApiModelProperty("开票时间")
    private Date invoiceTime;

    @ApiModelProperty("存储pdf文件key")
    private String pdfKey;

    @ApiModelProperty("存储png预览key")
    private String pngKey;

    @ApiModelProperty("开票状态:未开票,已开票,部分开票,已撤销,已作废")
    private Integer status;

    @ApiModelProperty("开票状态:未开票,已开票,部分开票,已撤销,已作废")
    private String statusName;

    @ApiModelProperty("撤销发票 默认0未发起，1撤销中， 3同意 4拒绝")
    private Integer invoiceReject;

    @ApiModelProperty("撤销发票拒绝原因")
    private String invoiceRejectReason;

    @ApiModelProperty("发票抬头")
    private String title;
    private String titleType;
    @ApiModelProperty("发票税号")
    private String dutyParagraph;

    @ApiModelProperty("开票结果详情")
    private String invoiceResult;

    @ApiModelProperty("扩展字段")
    private String features;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
