package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
@Api("撤销发票")
public class InvoiceRemoveRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "发票id")
    private Long id;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

    @ApiModelProperty(value = "买家id")
    private Long custId;

    @ApiModelProperty("卖家")
    private Long sellerId;

    @ApiModelProperty(value = "发票类型：0普通发票，1专票", required = true)
    private Integer invoiceType;

    @ApiModelProperty("关闭入口")
    private Boolean close = false;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(Objects.nonNull(primaryOrderId)||Objects.nonNull(id),I18NMessageUtils.getMessage("revoke.invoice")+"，"+I18NMessageUtils.getMessage("id.orderId.not.both.null"));  //# "撤销发票
        ParamUtil.nonNull(invoiceType, I18NMessageUtils.getMessage("invoice.type.cannot.be.empty"));  //# "发票类型不能为空"
        ParamUtil.expectTrue(Objects.nonNull(sellerId)||Objects.nonNull(custId),I18NMessageUtils.getMessage("buyer.seller.not.both.null"));

    }
}
