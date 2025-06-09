package com.aliyun.gts.gmall.center.trade.api.dto.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApiModel("申请发票结果: 已开具")
@Data
public class InvoiceApplyMessageRpcReq {
    @ApiModelProperty("税务发票平台对应生成的发票ID")
    private Long id;
    @ApiModelProperty("业务类型")
    private String bizType;
    @ApiModelProperty("发票的外部单号 -- 对应上游系统的发票请求号（requestNo）")
    private String requestNo;
    @ApiModelProperty("发票编码")
    private String invoiceCode;
    @ApiModelProperty("发票号")
    private String invoiceNo;
    @ApiModelProperty("含税金额")
    private BigDecimal invoiceAmountWithTax;
    @ApiModelProperty("不含税金额")
    private BigDecimal invoiceAmountWithoutTax;
    @ApiModelProperty("税额")
    private BigDecimal taxAmount;
    @ApiModelProperty("发票备注")
    private String memo;
    @ApiModelProperty("开票时间")
    private Date invoiceDate;
    @ApiModelProperty("发票状态")
    private String status;
    @ApiModelProperty("红票/蓝票\n" +
            "BLUE_INVOICE: 蓝票\n" +
            "RED_INVOICE: 红票")
    private String billingType;
    @ApiModelProperty("开具方式：线上/线下\n" +
            "ONLINE: 线上\n" +
            "DOWNLINE: 线下（导入导出）")
    private String issueType;
    @ApiModelProperty("税务通知单号(红字信息表编号)")
    private String taxNotifyNo;
    @ApiModelProperty("发票明细")
    private List<SingleDetail> detailList;
    @ApiModelProperty("是否寄出")
    private Boolean isSend;
    @ApiModelProperty("物流单号")
    private String trackingNumber;
    @ApiModelProperty("物流企业")
    private String logisticsCompay;
    @ApiModelProperty("是否是重新开具出来的")
    private Boolean isReissue;
    @ApiModelProperty("关联蓝票ID")
    private Long blueInvoiceId;


    @Data
    public static class SingleDetail {
        @ApiModelProperty("货物名称")
        private String goodsDescription;
        @ApiModelProperty("规格")
        private String specifications;
        @ApiModelProperty("单价")
        private BigDecimal unitAmount;
        @ApiModelProperty("单位")
        private String unit;
        @ApiModelProperty("数量")
        private Long quantity;
        @ApiModelProperty("含税金额")
        private BigDecimal invoiceAmountWithTax;
        @ApiModelProperty("不含税金额")
        private BigDecimal invoiceAmountWithoutTax;
        @ApiModelProperty("税额")
        private BigDecimal taxAmount;
        @ApiModelProperty("税率")
        private BigDecimal taxRate;
        @ApiModelProperty("税收编码")
        private String taxCode;
    }

}
