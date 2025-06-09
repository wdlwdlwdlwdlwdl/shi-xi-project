package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付渠道
 *
 * @author tiansong
 */
@ApiModel("支付渠道信息")
@Data
public class PayChannelVO {
    @ApiModelProperty("支付渠道")
    private String payChannel;
    @ApiModelProperty("支付渠道名称")
    private String payChannelName;
    @ApiModelProperty("支付渠道LOGO")
    private String payChannelLogo;
}
