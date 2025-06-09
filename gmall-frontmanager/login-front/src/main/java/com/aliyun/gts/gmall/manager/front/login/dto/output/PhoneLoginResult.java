package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机号登录结果
 *
 * @author tiansong
 */
@Data
public class PhoneLoginResult extends AbstractOutputInfo {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;
    @ApiModelProperty(value = "登录成功token", required = true)
    private String token;
    @ApiModelProperty(value = "注册成功/授权登录后的提示语", required = true)
    private String message;
    @ApiModelProperty(value = "是否需要重置密码", required = true)
    private boolean restPwd;
    @ApiModelProperty(value = "大客户入驻", required = true)
    private boolean b2bSettleIn;
}

