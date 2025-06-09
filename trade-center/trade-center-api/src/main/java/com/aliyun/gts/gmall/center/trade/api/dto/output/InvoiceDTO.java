package com.aliyun.gts.gmall.center.trade.api.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "发票信息")
public class InvoiceDTO extends AbstractOutputInfo {
    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("购方名称")
    private String title;
    @ApiModelProperty("发票代码")
    private String invoiceCode;
    @ApiModelProperty("发票号码")
    private String invoiceNo;
    @ApiModelProperty("开票日期")
    private Date invoiceDate;
    @ApiModelProperty("申请开票日期")
    private Date applyDate;
    @ApiModelProperty("合计金额")
    private String invoiceAmount;
    @ApiModelProperty("下载链接")
    private String url;

}
