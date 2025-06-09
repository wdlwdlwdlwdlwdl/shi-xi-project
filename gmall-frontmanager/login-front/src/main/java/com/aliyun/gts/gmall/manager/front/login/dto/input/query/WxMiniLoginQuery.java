package com.aliyun.gts.gmall.manager.front.login.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 微信小程序登录请求
 *
 * @author tiansong
 */
@ApiModel(description = "微信小程序授权登录")
@Data
public class WxMiniLoginQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "微信小程序授权code", required = true)
    private String code;
    @ApiModelProperty(value = "手机号码")
    private String phone;
    @ApiModelProperty(value = "验证码")
    private String securityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(code, I18NMessageUtils.getMessage("phone.required"));  //# "手机号码不能为空"
        if(StringUtils.isNotBlank(phone)) {
            ParamUtil.notBlank(securityCode, I18NMessageUtils.getMessage("captcha.required"));  //# "验证码不能为空"
            ParamUtil.expectTrue(phone.matches(BizConst.REGEX_MOBILE), I18NMessageUtils.getMessage("phone.format.invalid"));  //# "手机号码格式不正确"
        }
    }
}
