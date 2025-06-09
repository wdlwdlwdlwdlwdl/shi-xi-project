package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xinchen
 */
@Data
@ApiModel("支付确认返回参数")
public class ConfirmPayCheckRpcResp extends AbstractOutputInfo {

    @ApiModelProperty("支付成功确认")
    private Boolean paySuccess;
}
