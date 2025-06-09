package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

@Data
@Api(value = "发票详情")
public class InvoiceQueryRpcReq extends AbstractQueryRpcRequest {
    @ApiModelProperty(value = "主订单ID", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "发票id")
    private Long id;

    @ApiModelProperty(value = "买家id")
    private Long custId;

    @ApiModelProperty("卖家")
    private Long sellerId;
    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.expectTrue(Objects.nonNull(primaryOrderId)||Objects.nonNull(id),I18NMessageUtils.getMessage("invoice.query")+"，id和订单id不能同时为空");  //# "发票查询

        ParamUtil.expectTrue(Objects.nonNull(primaryOrderId)||Objects.nonNull(id),I18NMessageUtils.getMessage("invoice.query")+"，"+I18NMessageUtils.getMessage("id.orderId.not.both.null"));  //# "发票查询



        ParamUtil.expectTrue(Objects.nonNull(sellerId)||Objects.nonNull(custId),I18NMessageUtils.getMessage("buyer.seller.not.both.null"));


    }
}
