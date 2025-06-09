package com.aliyun.gts.gmall.platform.trade.common.util;

import org.apache.commons.lang3.StringUtils;

public class RouterIdUtils {

    private static final int LEN = 4;

    public static String getRouterId(String bizId) {
        if (StringUtils.isBlank(bizId)) {
            throw new IllegalArgumentException();
        }
        String rid = bizId;
        if (rid.length() > LEN) {
            rid = rid.substring(bizId.length() - LEN);
        }
        while (rid.length() < LEN) {
            rid = "0" + rid;
        }
        return rid;
    }
}
