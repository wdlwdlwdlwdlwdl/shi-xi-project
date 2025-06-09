package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.regex.Pattern;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改密码
 *
 * @author tiansong
 */
@ApiModel(description = "修改密码请求")
@Data
public class EditPasswordCommand extends LoginRestCommand {

    @ApiModelProperty(value = "原密码", required = true)
    private String oldPwd;
    @ApiModelProperty(value = "新密码", required = true)
    private String newPwd;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(oldPwd, I18NMessageUtils.getMessage("original.password.required")+"，"+I18NMessageUtils.getMessage("first.set.any.value"));  //# "原密码不能为空，初次设置可输入任意值"
        ParamUtil.notBlank(newPwd, I18NMessageUtils.getMessage("new.password.required"));  //# "新密码不能为空"
        checkPwd(newPwd);

    }

    private static void checkPwd(String newPassword) {
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\S]{8,}$";
        ParamUtil.expectTrue(Pattern.matches(passwordRegex, newPassword), "password.requirements");
    }
}