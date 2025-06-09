package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("合并支付请求出参")
public class OrderMergePayRpcResp extends AbstractOutputInfo {

    @ApiModelProperty(value = "合并支付每笔主单支付流水")
    private List<MergePayFlowInfo> mergePayFlowInfos;

    @ApiModelProperty(value = "合并支付表单信息")
    private String payData;

    @ApiModelProperty(value = "统一购物车标识")
    private String cartId;

    @ApiModelProperty(value = "支付token")
    private String payToken;
}
