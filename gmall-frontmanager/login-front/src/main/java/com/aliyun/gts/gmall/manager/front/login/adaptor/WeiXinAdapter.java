package com.aliyun.gts.gmall.manager.front.login.adaptor;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.login.dto.input.wechat.ShortLinkReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.WeiXinPhoneResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.WxMiniLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序适配器
 * @author tiansong
 */
@Slf4j
@Service
public class WeiXinAdapter {

    @Autowired
    private GmallFrontConfig gmallFrontConfig;

    private static final String ERROR_CODE_KEY = "errcode";
    private static final String OPENID_KEY     = "openid";
    private static final String PHONE_INFO     = "phone_info";
    private static final String ACCESS_TOKEN     = "access_token";

    private static final String LINK = "link";

    /**
     * 通过授权code，获取微信唯一标识信息【强依赖】
     *
     * @param code 授权码
     * @return 微信小程序账号信息
     */
    public WxMiniLoginResult getWxMiniLoginInfo(String code) {
        String appId = gmallFrontConfig.getWxMiniAppId();
        String secret = gmallFrontConfig.getWxMiniSecret();
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(secret)) {
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_CONFIG);
        }
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" + secret
            + "&js_code=" + code + "&grant_type=authorization_code";
        try {
            String jsonString = HttpUtil.get(requestUrl);
            if (StringUtils.isBlank(jsonString)) {
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH);
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            Integer errCode = jsonObject.getInteger(ERROR_CODE_KEY);
            if (errCode != null && !new Integer(0).equals(errCode)) {
                log.error("---getWxMiniLoginInfo-----error----:{}",jsonObject);
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH);
            }
            String openId = jsonObject.getString(OPENID_KEY);
            if (StringUtils.isBlank(openId)) {
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH);
            }
            return WxMiniLoginResult.builder().openId(openId).build();
        } catch (Exception e) {
            log.error("request=" + requestUrl, e);
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_AUTH);
        }
    }

    /**
     * 获取微信手机号码
     * @Author: alice
     * @param code:
     * @return
     */
    public WeiXinPhoneResult getWxPhoneNumber(String code) {
        if (StringUtils.isBlank(code)) {
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE_CODE);
        }
        String requestUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token="
                + getWeiXinAccessToken() +"";
        Map<String, Object> param = new HashMap<>(1);
        param.put("code", code);
        String jsonParam = JSONObject.toJSONString(param);
        try {
            String jsonString = HttpRequest.post(requestUrl)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(jsonParam)
                    .execute()
                    .body();
            if (StringUtils.isBlank(jsonString)) {
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE);
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            Integer errCode = jsonObject.getInteger(ERROR_CODE_KEY);
            if (errCode != null && !new Integer(0).equals(errCode)) {
                log.error("---getWxPhoneNumber-----error----:{}",jsonObject);
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE);
            }
            JSONObject phoneInfo = jsonObject.getJSONObject(PHONE_INFO);
            if (!phoneInfo.isEmpty()) {
                return phoneInfo.toJavaObject(WeiXinPhoneResult.class);
            }
        } catch (Exception e) {
            log.error("request=" + requestUrl, e);
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_PHONE);
        }
        return null;
    }

    private String getWeiXinAccessToken() {
        String appId = gmallFrontConfig.getWxMiniAppId();
        String secret = gmallFrontConfig.getWxMiniSecret();
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(secret)) {
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_CONFIG);
        }
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appId +"&secret="+secret+"";
        try {
            String  jsonString = HttpUtil.get(requestUrl);
            if (StringUtils.isBlank(jsonString)) {
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_ACCESS_TOKEN);
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            Integer errCode = jsonObject.getInteger(ERROR_CODE_KEY);
            if (errCode != null && !new Integer(0).equals(errCode)) {
                log.error("---getWeiXinAccessToken-----error----:{}",jsonObject);
                throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_ACCESS_TOKEN);
            }
            return jsonObject.getString(ACCESS_TOKEN);
        } catch (Exception e) {
            log.error("request=" + requestUrl, e);
            throw new FrontManagerException(LoginFrontResponseCode.THIRD_LOGIN_WECHAT_ACCESS_TOKEN);
        }
    }

    public String getShortLink(ShortLinkReq req) {
        String pageUrl = req.getPageUrl();
        if (StringUtils.isBlank(req.getPageUrl())) {
            throw new FrontManagerException(LoginFrontResponseCode.SOURCE_URL_IS_NULL);
        }
        String requestUrl = "https://api.weixin.qq.com/wxa/genwxashortlink?access_token=" + getWeiXinAccessToken();
        Map<String, Object> param = new HashMap<>();
        param.put("page_url", pageUrl);
        if (StringUtils.isNoneBlank(req.getPageTitle())) {
            param.put("page_title", req.getPageTitle());
        }
        if (req.getIsPermanent() != null) {
            param.put("is_permanent", req.getIsPermanent());
        }
        try {
            String jsonString = HttpRequest.post(requestUrl)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .body(JSONObject.toJSONString(param))
                    .execute()
                    .body();
            if (StringUtils.isBlank(jsonString)) {
                throw new FrontManagerException(LoginFrontResponseCode.GET_SHORT_LINK_FAIL);
            }
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            Integer errCode = jsonObject.getInteger(ERROR_CODE_KEY);
            if (errCode != null && !new Integer(0).equals(errCode)) {
                log.error("---获取短链失败---:{}", jsonObject);
                throw new FrontManagerException(LoginFrontResponseCode.GET_SHORT_LINK_FAIL);
            }
            return jsonObject.getString(LINK);
        } catch (Exception e) {
            log.error("request=" + requestUrl, e);
            throw new FrontManagerException(LoginFrontResponseCode.GET_SHORT_LINK_FAIL);
        }
    }

}