package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class OrderInvoiceListRpcReq extends AbstractPageQueryRpcRequest {

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
    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(invoiceType, I18NMessageUtils.getMessage("query.invoice.type.required"));
        ParamUtil.nonNull(isSearchCount, I18NMessageUtils.getMessage("invoice.query.total.cannot.be.empty"));  //# "查询总数不能为空"
        ParamUtil.expectTrue(Objects.nonNull(sellerId)||Objects.nonNull(custId),I18NMessageUtils.getMessage("invoice.query")+"，不允许买卖双方同时为空");  //# "发票查询
    }
}
