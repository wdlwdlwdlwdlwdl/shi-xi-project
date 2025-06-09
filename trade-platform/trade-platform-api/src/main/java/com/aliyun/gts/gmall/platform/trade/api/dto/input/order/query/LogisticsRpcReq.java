package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.QueryCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("物流详情")
public class LogisticsRpcReq extends QueryCommandRpcRequest {

    @ApiModelProperty(value = "主订单id", required = true)
    @NotNull
    private Long primaryOrderId;

    @ApiModelProperty(value = "主售后单id, 查退货物流使用")
    private Long primaryReversalId;

    @ApiModelProperty(value = "otp")
    private String otpCode;

    @ApiModelProperty(value = "returnCode")
    private String returnCode;

    private String logisticsUrl;

    private String logisticsId;

    private String deliveryServiceName;

    private String deliveryServiceCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId,  "[orderId] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "会员ID不能为空"
    }

}
