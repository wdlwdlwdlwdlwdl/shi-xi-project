package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@Api(value = "申请发票")
public class InvoiceApplyRpcReq extends AbstractQueryRpcRequest {
    @ApiModelProperty(value = "主订单id", required = true)
    private Long primaryOrderId;
    @ApiModelProperty(value = "买家id")
    private Long custId;

    @ApiModelProperty("卖家")
    private Long sellerId;

    @ApiModelProperty(value = "发票类型：0普通发票，1专票", required = true)
    private Integer invoiceType;

    @ApiModelProperty("发票抬头id")
    private Long titleId;

    @ApiModelProperty("专票信息")
    private SpecialInvoice specialInvoice;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId,"main.order.id.not.null");
        ParamUtil.nonNull(invoiceType,I18NMessageUtils.getMessage("invoice.type.cannot.be.empty"));  //# "发票类型不能为空"
        if (Objects.equals(invoiceType, InvoiceTypeEnum.GENERAL.getCode())){
            ParamUtil.nonNull(titleId,"invoice.title.id.required");
            ParamUtil.nonNull(custId,"buyer.id.required");
        } else if (Objects.equals(invoiceType, InvoiceTypeEnum.SPECIAL.getCode())){
            ParamUtil.nonNull(specialInvoice,I18NMessageUtils.getMessage("special.invoice.info.cannot.be.empty"));  //# "专票信息不能为空"
            ParamUtil.nonNull(sellerId,"seller.id.required");
            specialInvoice.checkInput();
        }else {
            throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("invoice.type.not.normal")+" / "+I18NMessageUtils.getMessage("vat.invoice"));  //# "发票类型不是普通发票/专票"
        }
    }

    @Data
    public static class SpecialInvoice extends AbstractQueryRpcRequest{
        @ApiModelProperty("id")
        private Long id;
        @ApiModelProperty("主订单id")
        private Long primaryOrderId;

        @ApiModelProperty("发票代码")
        private String invoiceCode;

        @ApiModelProperty("发票号码")
        private String invoiceNo;

        @ApiModelProperty("开票金额")
        private String invoiceAmount;

        @ApiModelProperty("开票时间")
        private Date invoiceTime;

        public void checkInput() {
            ParamUtil.nonNull(primaryOrderId, "main.order.id.required");
            ParamUtil.notBlank(invoiceCode, I18NMessageUtils.getMessage("invoice.code.cannot.be.empty"));  //# "发票编码不能为空"
            ParamUtil.notBlank(invoiceNo, I18NMessageUtils.getMessage("invoice.number.cannot.be.empty"));  //# "发票号不能为空"
            ParamUtil.notBlank(invoiceAmount, I18NMessageUtils.getMessage("invoice.status.cannot.be.empty"));  //# "开票状态不能为空"
            ParamUtil.nonNull(invoiceTime, "main.order.id.required");


        }
    }
}
