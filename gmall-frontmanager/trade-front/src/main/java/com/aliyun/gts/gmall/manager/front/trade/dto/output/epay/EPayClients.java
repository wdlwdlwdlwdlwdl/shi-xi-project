package com.aliyun.gts.gmall.manager.front.trade.dto.output.epay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EPayClients extends AbstractOutputInfo {

    @ApiModelProperty("grantType")
    private String grantType;

    private String scope;

    @ApiModelProperty("clientId")
    private String clientId;

    @ApiModelProperty("clientSecret")
    private String clientSecret;

    @ApiModelProperty("terminal")
    private String terminal;

    @ApiModelProperty("币种")
    private String currency;

    @ApiModelProperty("token")
    private String accessToken;

    @ApiModelProperty("expiresIn")
    private String expiresIn;

    @ApiModelProperty("mock")
    private String mock;
}
