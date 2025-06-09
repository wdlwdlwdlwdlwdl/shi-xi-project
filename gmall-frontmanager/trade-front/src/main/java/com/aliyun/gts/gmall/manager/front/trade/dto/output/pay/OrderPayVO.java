package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单支付信息
 *
 * @author tiansong
 */
@ApiModel("订单支付信息")
@Data
public class OrderPayVO extends AbstractOutputInfo {
    @ApiModelProperty("支付流水号")
    private String  payFlowId;
    @ApiModelProperty("支付结果表单信息（非积分等内部支付）")
    private String  payData;
    @ApiModelProperty(value = "统一购物车标识")
    private String cartId;
    @ApiModelProperty(value = "支付token")
    private String payToken;

}
