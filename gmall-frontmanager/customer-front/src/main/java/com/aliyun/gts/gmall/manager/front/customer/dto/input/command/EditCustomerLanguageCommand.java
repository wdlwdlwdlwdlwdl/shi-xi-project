package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@ApiModel(description = "修改用户语言信息")
public class EditCustomerLanguageCommand extends LoginRestCommand {

    @ApiModelProperty("语言")
    private String language;


    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectFalse(StringUtils.isAllBlank(language), I18NMessageUtils.getMessage("update.user.language.not.empty"));  //# "更新用户信息不能全空"
    }

}
