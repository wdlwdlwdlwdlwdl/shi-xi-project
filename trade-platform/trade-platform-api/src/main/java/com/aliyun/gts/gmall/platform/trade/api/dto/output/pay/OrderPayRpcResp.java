package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付请求出参
 *
 * @author xinche
 */
@Data
@ApiModel("支付请求出参")
public class OrderPayRpcResp extends AbstractOutputInfo {

    @ApiModelProperty(value = "支付流水号")
    private String payFlowId;

    @ApiModelProperty(value = "支付结果表单信息")
    private String payData;

    @ApiModelProperty(value = "统一购物车标识")
    private String cartId;

    @ApiModelProperty(value = "支付token")
    private String payToken;

}
