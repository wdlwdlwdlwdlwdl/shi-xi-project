package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;


import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("合并支付每笔主单支付流水")
@Data
public class MergePayFlowInfo extends AbstractOutputInfo {

    @ApiModelProperty(value = "支付流水号")
    private String payFlowId;

    @ApiModelProperty(value = "主订单号")
    private String primaryOrderId;

}
