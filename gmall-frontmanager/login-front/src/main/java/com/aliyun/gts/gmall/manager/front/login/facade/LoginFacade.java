package com.aliyun.gts.gmall.manager.front.login.facade;

import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.LogoutRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ResetPwdRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.CasLoginQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.CheckLoginQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.PhoneLoginQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.WxMiniLoginQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CaptchaResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CasLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.PhoneLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.WeiXinPhoneResult;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;

/**
 * 注册登录相关接口
 *
 * @author tiansong
 * @date 2021-01-29
 */
public interface LoginFacade {

    /**
     * 手机登录发送验证码
     *
     * @param securityCodeQuery
     * @return
     */
    SecurityCodeResult sendSecurityCode(SecurityCodeQuery securityCodeQuery);

    /**
     * 手机号码登录
     *
     * @param phoneLoginQuery
     * @return
     */
    PhoneLoginResult phoneLogin(PhoneLoginQuery phoneLoginQuery);

    /**
     * Cas登录
     *
     * @param casLoginQuery
     * @return
     */
    RestResponse<CasLoginResult> casLogin(CasLoginQuery casLoginQuery);

    /**
     * 微信小程序登录
     *
     * @param wxMiniLoginQuery
     * @return
     */
    PhoneLoginResult wxMiniLogin(WxMiniLoginQuery wxMiniLoginQuery);

    /**
     * 用户登出
     *
     * @param logoutRestCommand
     * @return
     */
    Boolean logout(LogoutRestCommand logoutRestCommand);

    /**
     * 校验用户是否登录
     *
     * @param checkLoginQuery 请求
     * @return 是否登录
     */
    String checkLogin(CheckLoginQuery checkLoginQuery);

    boolean resetPwd(ResetPwdRestCommand restCommand);

    String grantIntegral(String phone,Integer checkReceivePointType);

    String grantIntegralByPrimary(String iin, Integer checkReceivePointType);
    
    /**
     * 微信小程序获取手机号码
     * @Author: alice
     * @param wxMiniLoginQuery:
     * @return
     */
    WeiXinPhoneResult getWxPhone(WxMiniLoginQuery wxMiniLoginQuery);

    Boolean refreshToken(Long custId, ChannelEnum channelEnum, String newToken);

    PhoneLoginResult getLoginResult(CustomerDTO customerDTO, ChannelEnum channelEnum);

    CaptchaResponse genImageCaptchaImage();

}
