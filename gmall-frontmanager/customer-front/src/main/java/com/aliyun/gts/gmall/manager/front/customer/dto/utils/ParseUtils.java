package com.aliyun.gts.gmall.manager.front.customer.dto.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/9 15:14
 */
public class ParseUtils {

    public static String urlDecode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
