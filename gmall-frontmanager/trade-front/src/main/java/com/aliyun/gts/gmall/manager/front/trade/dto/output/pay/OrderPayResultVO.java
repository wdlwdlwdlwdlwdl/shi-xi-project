package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderPayResultVO extends AbstractOutputInfo {

    @ApiModelProperty("支付流水号")
    private String  payFlowId;

    @ApiModelProperty("主订单号")
    private String primaryOrderId;

    @ApiModelProperty("支付结果表单信息（非积分等内部支付）")
    private Object payData;

    @ApiModelProperty("支付成功检查页面")
    private String paySuccessUrl;

    @ApiModelProperty("纯积分支付、mock支付，跳转支付成功页面")
    private boolean directPaySuccess;
}
