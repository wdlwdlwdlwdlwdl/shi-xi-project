package com.aliyun.gts.gmall.manager.front.login.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.regex.Pattern;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractCommandRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户重置密码")
@Data
public class ResetPwdRestCommand extends AbstractCommandRestRequest {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    private String securityCode;

    @ApiModelProperty(value = "新密码", required = true)
    private String newPwd;

    @Override
    public void checkInput() {

        ParamUtil.notBlank(phone, I18NMessageUtils.getMessage("phone.required"));  //# "手机号码不能为空"

        ParamUtil.notBlank(securityCode, I18NMessageUtils.getMessage("phone.captcha.required"));  //# "手机验证码不能为空"

        ParamUtil.notBlank(newPwd, I18NMessageUtils.getMessage("new.password.required"));  //# "新密码不能为空"

        checkPwd(newPwd);

    }

    private static void checkPwd(String newPassword) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\S]{8,}$";
        ParamUtil.expectTrue(Pattern.matches(passwordRegex, newPassword), "password.requirements");
    }

}
