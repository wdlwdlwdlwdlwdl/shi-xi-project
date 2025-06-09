package com.aliyun.gts.gmall.platform.trade.api.dto.input.pay;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author xinchen
 */
@Data
@ApiModel("支付回调入参")
public class OrderPayCallBackRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty(value = "下单渠道", required = true)
    private String orderChannel;
    @ApiModelProperty(value = "支付回调参数", required = true)
    private Map<String, Object> callBackParams;

}
