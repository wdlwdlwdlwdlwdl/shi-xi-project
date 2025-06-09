package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("确认订单支付方式")
public class PayTypeDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "支付方式", required = true)
    private String payType;

    @ApiModelProperty(value = "支付方式名称", required = true)
    private String payTypeName;
}
