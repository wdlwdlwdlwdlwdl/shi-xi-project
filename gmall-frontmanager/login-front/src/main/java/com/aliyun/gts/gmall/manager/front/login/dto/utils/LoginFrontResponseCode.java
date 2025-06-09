package com.aliyun.gts.gmall.manager.front.login.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 登录模块的响应码
 *
 * @author tiansong
 */
public enum LoginFrontResponseCode implements ResponseCode {
    CUSTOMER_NOT_EXIST("60010001", "|user.info.not.exist|，|try.again|！",  0)  //# "用户信息不存在，请重试
    ,LOGIN_FAIL("60010002", "|login.fail|，|try.again|！",  0)  //# "登录失败，请重试
    ,MEMBER_CREATE_FAIL("60010003", "|user.creation.fail|，|try.again|！",  0)  //# "用户创建失败，请重试
    ,CHECK_PASSWORD_FAIL("60010004", "|password.verification.fail|，|try.again|！",  0)  //# "密码验证失败，请重试
    ,CHECK_SECURITY_CODE_FAIL("60010005", "|captcha.verification.fail|，|try.again|！",  0)  //# "验证码验证失败，请重试
    ,SEND_SECURITY_CODE_FAIL("60010006", "|captcha.send.fail|，|try.again|！",  0)  //# "验证码发送失败，请重试
    ,PHONE_BIND_FAIL("60010007", "|user.phone.binding.fail|，|try.again|！",  0)  //# "用户手机绑定失败，请重试
    ,WECHAT_BIND_FAIL("60010008", "|wechat.binding.fail|，|try.again|！",  0)  //# "微信绑定失败，请重试
    ,MEMBER_SAVE_FAIL("60010009", "|user.update.fail|，|try.again|！",  0)  //# "用户更新失败，请重试
    ,CUSTOMER_QUERY_FAIL("60010010", "|user.query.fail|",  0)  //# "用户查询失败"
    ,CUSTOMER_APPLY_CANNOT_EDIT("60010011", "|profile.uneditable|",  0)  //# "入驻资料不能修改"
    , CUSTOMER_RESET_PWD_FAIL("60010012", "|reset.password.fail|",  0)  //# "重置密码失败"
    ,CHECK_SECURITY_FREQUENCE_LIMIT_FAIL("60010019", "|captcha.send.frequent|！",  0)  //# "请勿频繁发送验证码
    ,APP_BIND_FAIL("60010013", "|tm.binding.fail|，|try.again|！",  0)  //# "天猫精灵绑定失败，请重试
    ,PHONE_FAIL("60010018", "|phone.invalid|，|verify.and.retry|！",  0)  //# "您输入的手机号不正确，请核实后重新输入
    ,APP_BIND_JWT_FAIL("60010013", "|tm.data.error|，|try.again|！",  0)  //# "解析天猫精灵数据错误，请重试
    ,WECHAT_UNBIND_FAIL("60010118", "|wechat.unbind.fail|，|try.again|！",  0)  //# "微信解绑定失败，请重试
    ,CUSTOMER_EXIST("60010016", "|user.exist|，|cannot.re.register|！",  0)  //# "用户已存在，不能再注册
    ,DECRYPT_PASSWORD_FAIL("60010020", "|password.verification.fail|，|try.again|！",  0)  //# "密码验证失败，请重试
    ,SHORT_URL_ERROR("60010022", "|shortlink.creation.fail|，|try.again|！",  0)  //# "生成短链失败，请重试
    ,CHECK_SECURITY_LIMIT("60010023", "|captcha.frequent|！",  0)  //# "请勿频繁尝试验证码
    ,VERIFICATION_CODE_ERROR("60010024", "|captcha.error|，|try.again|！",  0)  //# "图形验证码错误，请重试
    ,VERIFICATION_CODE_NOT_EXIST("60010025", "|captcha.not.entered|，|try.again|！",  0)  //# "图形验证码未输入，请重试
    ,VERIFICATION_CODE_EXPIRED_ERROR("60010026", "|captcha.expired|，|captcha.renew|！",  0)  //# "图形验证码已过期或失效，请重新获取图像验证码






    // 第三方登录相关
    ,THIRD_LOGIN_WECHAT_CONFIG("60010060", "|wechat.auth.config.empty|，|contact.admin|！",  0)  //# "微信授权配置为空，请联系管理员
    ,THIRD_LOGIN_WECHAT_AUTH("60010061", "|wechat.auth.fail|，|try.again|！",  0)  //# "微信授权访问失败，请重试
    ,THIRD_LOGIN_WECHAT_ACCESS_TOKEN("60010062", "|wechat.fetch|token|try.again|，请重试！",  0)  //# "微信获取token失败
    ,THIRD_LOGIN_WECHAT_PHONE_CODE("60010062", "|try.again|code缺失，请重试！",  0)  //# "微信获取手机号码
    ,THIRD_LOGIN_WECHAT_PHONE("60010062", "|wechat.phone.fail|，|try.again|！",  0),  //# "微信获取手机号码失败，请重试


    ET_SHORT_LINK_USER_TOKEN_FAIL("60010064", "|shortlink.fetch.fail|（token |invalid|）",  0),  //# "获取短链失败（token 失效
    SOURCE_URL_IS_NULL("60010065", "|source.link.empty|",  0),  //# "源链接信息为空"
    GET_SHORT_LINK_FAIL("60010066", "|shortlink.fetch.fail|",  0),  //# "获取短链失败"

    IIN_IS_NULL("60010076", "|iin.empty|",  0),  //# "源链接信息为空"

    ILLEGAL_LOGIN_TYPE("60010078", "|login.type.illegal|",  0),  //# "源链接信息为空"
    ;

    private String code;
    private String script;
    private int args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private LoginFrontResponseCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
