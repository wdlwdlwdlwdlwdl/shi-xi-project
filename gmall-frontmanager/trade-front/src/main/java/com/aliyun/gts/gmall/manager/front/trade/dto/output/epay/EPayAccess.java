package com.aliyun.gts.gmall.manager.front.trade.dto.output.epay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EPayAccess extends AbstractOutputInfo {

    @ApiModelProperty("请求epay的 accessToken ")
    private String accessToken;

    @ApiModelProperty("过期时间")
    private String expiresIn;


}
