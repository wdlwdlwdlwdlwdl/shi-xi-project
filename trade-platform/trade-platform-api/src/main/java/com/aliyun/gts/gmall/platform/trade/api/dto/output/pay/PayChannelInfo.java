package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xinchen
 */
@Data
@ApiModel("支付渠道信息")
public class PayChannelInfo extends AbstractOutputInfo {

    @ApiModelProperty(value="支付方法")
    private String payMethod;

    @ApiModelProperty(value="支付方法名称")
    private String payMethodName;

    @ApiModelProperty(value="支付渠道")
    private String payChannel;

    @ApiModelProperty(value="支付渠道名称")
    private String payChannelName;

}
