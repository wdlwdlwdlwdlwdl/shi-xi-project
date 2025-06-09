package com.aliyun.gts.gmall.platform.trade.common.util;

public class NumUtils {

    public static long getNullZero(Long v) {
        return v == null ? 0 : v.longValue();
    }
}
