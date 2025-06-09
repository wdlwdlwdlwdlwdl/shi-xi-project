package com.aliyun.gts.gmall.manager.front.login.adaptor;

import com.aliyun.gts.gmall.manager.front.login.dto.output.WeiXinPhoneResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.WxMiniLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第三方适配器
 * @author alice
 * @version 1.0.0
 * @createTime 2022年10月09日 18:05:00
 */
@Slf4j
@Service
public class ThirdAdapter {

    @Autowired
    private WeiXinAdapter weiXinAdapter;

    public WxMiniLoginResult getWxMiniLoginInfo(String code) {
        try {
            return weiXinAdapter.getWxMiniLoginInfo(code);
        } catch (Exception e) {
            log.error(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH.getCode(),
                    LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH.getMessage());
        }
        return null;
    }

    public WeiXinPhoneResult getWxPhoneNumber(String code) {
        try {
            return weiXinAdapter.getWxPhoneNumber(code);
        } catch (Exception e) {
            log.error(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE.getCode(),
                    LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE.getMessage());
        }
        return null;
    }

}
