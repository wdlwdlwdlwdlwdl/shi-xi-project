package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 修改用户头像
 *
 * @author tiansong
 */
@ApiModel(description = "修改用户头像")
@Data
public class EditHeadUrlCommand extends LoginRestCommand {
    @ApiModelProperty(value = "用户头像", required = true)
    private String headUrl;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(headUrl, I18NMessageUtils.getMessage("user.avatar.required"));  //# "用户头像不能为空"
    }
}