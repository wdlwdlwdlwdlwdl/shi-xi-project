package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/4 11:07
 */
public class Md5Utils {

    public static String md5(String context) {
        return DigestUtils.md5Hex(context);
    }
}
