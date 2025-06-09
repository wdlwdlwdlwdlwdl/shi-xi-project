package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机号登录结果
 *
 * @author tiansong
 */
@Data
public class CasLoginResult extends AbstractOutputInfo {

    @ApiModelProperty(value = "刷新token", required = true)
    private String refreshToken;
    @ApiModelProperty(value = "登录成功token", required = true)
    private String token;
    @ApiModelProperty(value = "注册成功/授权登录后的提示语", required = true)
    private String message;
    @ApiModelProperty(value = "是否需要重置密码", required = true)
    private boolean restPwd;
    @ApiModelProperty(value = "大客户入驻", required = true)
    private boolean b2bSettleIn;
    @ApiModelProperty(value = "语言")
    private String language;
    @ApiModelProperty(value = "当前登录用户")
    private CustomerVO currentUser;


}

