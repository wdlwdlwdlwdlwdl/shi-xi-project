package com.aliyun.gts.gmall.manager.front.login.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.user.common.enums.MobileRegularExpEnum;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 15:22:00
 */
@Data
public class UnbindCustomerReq extends LoginRestCommand {

    @ApiModelProperty(value = "第三方应用的openId")
    private String openId;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "类型", required = true)
    private String type;

    @Override
    public void checkInput() {
        super.checkInput();
        if (StringUtils.isNotBlank(phone)) {
            ParamUtil.expectTrue(MobileRegularExpEnum.isMobileNumber(phone), LoginFrontResponseCode.PHONE_FAIL.getMessage());
        }
        ParamUtil.notBlank(type, I18NMessageUtils.getMessage("type.required"));  //# "类型不能为空"
    }

}
