package com.aliyun.gts.gmall.manager.front.login.dto.input.query;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 手机验证码发送请求
 *
 * @author tiansong
 */
@ApiModel(description = "手机验证码发送请求")
@Data
public class SecurityCodeQuery extends AbstractQueryRestRequest {
    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;

    @ApiModelProperty(value = "是否校验用户存在")
    private boolean checkUserExist = false;

    @ApiModelProperty(value = "验证码类型,login:登陆,resetPwd:重置密码")
    private String sendType;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(phone, I18NMessageUtils.getMessage("phone.required"));  //# "手机号码不能为空"
        ParamUtil.expectTrue(phone.matches(BizConst.REGEX_MOBILE), I18NMessageUtils.getMessage("phone.format.invalid"));  //# "手机号码格式不正确"
    }
}
