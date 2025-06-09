package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfirmStepExtendDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "手笔支付")
    private PayPriceDTO firstPay;

    @ApiModelProperty(value = "剩余支付(大于2阶段的为剩下合计)")
    private PayPriceDTO remainPay;
}
