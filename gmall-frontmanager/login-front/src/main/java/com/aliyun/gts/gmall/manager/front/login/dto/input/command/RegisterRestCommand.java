package com.aliyun.gts.gmall.manager.front.login.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@ApiModel("用户注册")
@Data
public class RegisterRestCommand extends AbstractCommandRestRequest {

    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "验证码", required = true)
    private String securityCode;
    @ApiModelProperty("features")
    private Map<String, String> features;
    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(username, I18NMessageUtils.getMessage("username.required"));  //# "用户名不能为空"
        ParamUtil.nonNull(phone, I18NMessageUtils.getMessage("phone.required"));  //# "手机号码不能为空"
        ParamUtil.nonNull(password, I18NMessageUtils.getMessage("password.required"));  //# "密码不能为空"
        ParamUtil.nonNull(securityCode, I18NMessageUtils.getMessage("captcha.required"));  //# "验证码不能为空"

        checkLen(username, 3, 20, I18NMessageUtils.getMessage("username.length.range")+" [3,20]");  //# "用户名长度必须在
        checkLen(password, 3, 20, I18NMessageUtils.getMessage("password.length.range")+" [3,20]");  //# "密码长度必须在
    }

    public static void checkLen(String s, int min, int max, String message) {
        if (s.length() < min || s.length() > max) {
            ParamUtil.expectTrue(false, message);
        }
    }
}
