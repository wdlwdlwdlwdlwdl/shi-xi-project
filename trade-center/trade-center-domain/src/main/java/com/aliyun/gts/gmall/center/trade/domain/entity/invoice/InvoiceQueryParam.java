package com.aliyun.gts.gmall.center.trade.domain.entity.invoice;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceQueryParam {

    private Integer pageNum;

    private Integer pageSize;

    @ApiModelProperty("主订单id")
    private Long primaryOrderId;

    @ApiModelProperty("销售类型")
    private Integer saleType;

    @ApiModelProperty("发票类型：0普通发票，1专票")
    private Integer invoiceType;

    @ApiModelProperty("发票代码")
    private String invoiceCode;

    @ApiModelProperty("发票号码")
    private String invoiceNo;

    @ApiModelProperty("开票状态")
    private Integer status;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("买家ID")
    private Long custId;

    @ApiModelProperty("总数查询开关")
    private Boolean isSearchCount;

    @ApiModelProperty("开票开始时间")
    private Date startTime;

    @ApiModelProperty("开票结束时间")
    private Date endTime;

}
