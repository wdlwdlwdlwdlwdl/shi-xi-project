package com.aliyun.gts.gmall.platform.trade.domain.util;

public class NumUtils {

    public static long getNullZero(Long v) {
        return v == null ? 0 : v.longValue();
    }

    public static int getNullZero(Integer v) {
        return v == null ? 0 : v.intValue();
    }
}
