package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.annotation.OpLog;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.enums.BizCodeEnum;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.enums.OpCodeEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.LogoutRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ResetPwdRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CaptchaResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CasLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.PhoneLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.WeiXinPhoneResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.bytecode.Opcode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static com.aliyun.gts.gmall.manager.front.login.common.SecurityCodeType.LOGIN;
import static com.aliyun.gts.gmall.manager.front.login.common.SecurityCodeType.RESET_PWD;

/**
 * 登录
 *
 * @author tiansong
 */
@RestController
@Api(value = "登录相关操作", tags = {"login"})
public class LoginController {

    @Autowired
    private LoginFacade loginFacade;

    @Value("${front-manager.login.type:PHONE}")
    private String loginType;

    @ApiOperation(value = "手机登录发送验证码")
    @PostMapping(name = "sendSecurityCode", value = "/api/login/sendSecurityCode")
    public @ResponseBody
    RestResponse<SecurityCodeResult> sendSecurityCode(@RequestBody SecurityCodeQuery securityCodeQuery) {

        securityCodeQuery.setSendType(LOGIN);

        return RestResponse.okWithoutMsg(loginFacade.sendSecurityCode(securityCodeQuery));
    }

    @ApiOperation(value = "登录类型")
    @PostMapping(name = "loginType", value = "/api/login/type")
    public @ResponseBody RestResponse<String> type(EmptyRestQuery emptyRestQuery) {
        return RestResponse.okWithoutMsg(loginType);
    }

    @ApiOperation(value = "手机号码登录")
    @PostMapping(name = "phoneLogin", value = "/api/login/phoneLogin")
    public @ResponseBody
    RestResponse<PhoneLoginResult> phoneLogin(@RequestBody PhoneLoginQuery phoneLoginQuery) {
        if (!"PHONE".equals(loginType)) {
            return RestResponse.fail(LoginFrontResponseCode.ILLEGAL_LOGIN_TYPE);
        }
        return RestResponse.okWithoutMsg(loginFacade.phoneLogin(phoneLoginQuery));
    }

    @ApiOperation(value = "cas登录")
    @PostMapping(name = "casLogin", value = "/api/login/casLogin")
    public @ResponseBody
    RestResponse<CasLoginResult> casLogin(@RequestBody CasLoginQuery casLoginQuery) {
        if (!"CAS".equals(loginType)) {
              return RestResponse.fail(LoginFrontResponseCode.ILLEGAL_LOGIN_TYPE);
        }
        return loginFacade.casLogin(casLoginQuery);
    }

    /**
     * 自动登录校验 用于日志保存
     * @param
     * @return
     */
    @ApiOperation(value = "自动登录")
    @PostMapping(name = "autoLoginCheck", value = "/api/login/autoLoginCheck")
    @OpLog(bizCode = BizCodeEnum.LOGIN, opCode = OpCodeEnum.SHOP_LOGIN)
    public @ResponseBody
    RestResponse<Boolean> autoLoginCheck(@RequestBody AutoLoginCheckQuery autoLoginCheckQuery) {
        return RestResponse.okWithoutMsg(UserHolder.getUser() != null);
    }

    @ApiOperation(value = "用户主动登出")
    @PostMapping(name = "logout", value = "/api/login/logout")
    public @ResponseBody
    RestResponse<Boolean> logout(@RequestBody LogoutRestCommand logoutRestCommand) {
        return RestResponse.okWithoutMsg(loginFacade.logout(logoutRestCommand));
    }

    @ApiOperation(value = "用户主动登出")
    @PostMapping(name = "checkLogin", value = "/api/login/checkLogin")
    public @ResponseBody
    RestResponse<String> checkLogin(@RequestBody CheckLoginQuery checkLoginQuery) {
        return RestResponse.okWithoutMsg(loginFacade.checkLogin(checkLoginQuery));
    }

    @ApiOperation(value = "微信小程序获取手机号码")
    @PostMapping(name = "wxMiniPhone", value = "/api/login/wxMiniPhone")
    public @ResponseBody
    RestResponse<WeiXinPhoneResult> wxMiniPhone(@RequestBody WxMiniLoginQuery wxMiniLoginQuery) {
        return RestResponse.okWithoutMsg(loginFacade.getWxPhone(wxMiniLoginQuery));
    }


    @ApiOperation(value = "微信小程序登录")
    @PostMapping(name = "wxMiniLogin", value = "/api/login/wxMiniLogin")
    public @ResponseBody
    RestResponse<PhoneLoginResult> wxMiniLogin(@RequestBody WxMiniLoginQuery wxMiniLoginQuery) {
        return RestResponse.okWithoutMsg(loginFacade.wxMiniLogin(wxMiniLoginQuery));
    }

    @ApiOperation(value = "重置用户密码")
    @PostMapping(name = "resetPwd", value = "/api/login/resetPwd")
    public @ResponseBody
    RestResponse<Boolean> resetPwd(@RequestBody ResetPwdRestCommand restCommand) {
        return RestResponse.okWithoutMsg(loginFacade.resetPwd(restCommand));
    }

    @ApiOperation(value = "重置用户密码验证吗")
    @PostMapping(name = "sendResetPwdCode", value = "/api/login/sendResetPwdCode")
    public @ResponseBody
    RestResponse<SecurityCodeResult> sendResetPwdCode(@RequestBody SecurityCodeQuery securityCodeQuery) {
        securityCodeQuery.setSendType(RESET_PWD);
        return RestResponse.okWithoutMsg(loginFacade.sendSecurityCode(securityCodeQuery));
    }

    @ApiOperation(value = "获取图片验证码")
    @PostMapping(name = "getCaptcha", value = "/api/login/getCaptcha")
    public @ResponseBody
    RestResponse<CaptchaResponse> getCaptcha(@RequestBody GetCaptcha getCaptcha) {
        return RestResponse.okWithoutMsg(loginFacade.genImageCaptchaImage());
    }
}