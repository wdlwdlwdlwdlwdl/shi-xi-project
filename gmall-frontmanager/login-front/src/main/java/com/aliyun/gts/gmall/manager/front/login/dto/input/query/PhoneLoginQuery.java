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
 * 手机登录请求
 *
 * @author tiansong
 */
@ApiModel(description = "手机登录，支持：手机号+验证码，手机号+密码")
@Data
public class PhoneLoginQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;

    @ApiModelProperty(value = "验证码")
    private String securityCode;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "是否自动注册")
    private boolean autoRegister = true;

    @ApiModelProperty(value = "图片验证码的缓存key")
    private String imageKey;

    @ApiModelProperty(value = "用户输入的图片验证码")
    private String imageValue;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(phone, I18NMessageUtils.getMessage("phone.required"));  //# "手机号码不能为空"
        ParamUtil.expectTrue(phone.matches(BizConst.REGEX_MOBILE), I18NMessageUtils.getMessage("phone.format.invalid"));  //# "手机号码格式不正确"
        // 密码和验证码二选一，不能都为空
        ParamUtil.expectFalse(StringUtils.isAllBlank(securityCode, password), I18NMessageUtils.getMessage("password.or.captcha.required"));  //# "请输入密码或验证码"
    }
}
