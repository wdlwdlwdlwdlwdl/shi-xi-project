package com.aliyun.gts.gmall.manager.front.login.dto.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 登录相关操作
 *
 * @author tiansong
 */
@Slf4j
public class LoginUtils {

    private static final int HEX_VALUE_COUNT = 16;
    /**
     * 基础的100亿
     */
    public static final long BASE_ID = 10000000000L;

    public static long convertToCustId(String openId) {
        if (StringUtils.isBlank(openId)) {
            return 0L;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str = new char[16 * 2];
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(openId.getBytes());
            byte[] tmp = md.digest();
            int k = 0;
            for (int i = 0; i < HEX_VALUE_COUNT; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
        } catch (Exception e) {
            log.error("value=" + openId, e);
        }
        int result = new String(str).hashCode();
        return result > 0 ? (BASE_ID + result) : (BASE_ID - result);
    }

    public static void main(String[] args) {
        System.out.println(convertToCustId("ApplicationFilterChain.java:193)"));
        System.out.println(convertToCustId("061zEGFa1Yv1SA02AfHa1sTjk93zEGFt"));
    }
}
