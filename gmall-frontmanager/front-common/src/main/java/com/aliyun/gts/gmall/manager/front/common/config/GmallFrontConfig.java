package com.aliyun.gts.gmall.manager.front.common.config;

import com.aliyun.gts.gmall.manager.front.common.consts.ChannelEnum;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gmall ACM(应用配置管理)
 *
 * @author tiansong
 */
@Component
@Getter
public class GmallFrontConfig {
    /**
     * 设置跨域允许的域名
     */
    @Value(value = "${corsconfig.addallowedorigin:*}")
    private String[] addAllowedOrigin;

    @Value(value = "${degradation.trade.callBackHost:http://47.110.139.157:7001/}")
    private String serverHost;
    @Value(value = "${gmall.pay.successUrl:http://platform-k8s-gmall-frontmanager.iwhalewell.com/member/mobilePaySuccess.html#/paySuccess?}")
    private String paySuccessUrl;
    @Value(value = "${gmall.pay.successUrlPc:http://platform-k8s-gmall-frontmanager.iwhalewell.com/member/pcPaySuccess.html#/paySuccess?}")
    private String paySuccessUrlPc;

    public String getPaySuccessUrl(String orderChannel) {
        if (ChannelEnum.PC.getCode().equals(orderChannel)) {
            return paySuccessUrlPc;
        }
        return paySuccessUrl;
    }

    /**
     * reids cache config
     */
    @Value("${gmall.cache.multi.host}")
    private String redisHost;
    @Value("${gmall.cache.multi.port}")
    private int    redisPort;
    @Value("${gmall.cache.multi.auth}")
    private String redisAuth;

    /**
     * 微信小程序
     */
    @Value("${third.login.wxMiniAppId:}")
    private String wxMiniAppId;
    @Value("${third.login.wxMiniSecret:}")
    private String wxMiniSecret;
    /**
     * 是否使用kong
     */
    @Value(value = "${corsconfig.useKong:}")
    private Boolean useKong = true;

    /**
     * 天猫精灵APP，解密jwt，秘钥
     */
    @Value("${tmallgenie.jwt.secret}")
    private String tmallGenieJwtSecret;
}
