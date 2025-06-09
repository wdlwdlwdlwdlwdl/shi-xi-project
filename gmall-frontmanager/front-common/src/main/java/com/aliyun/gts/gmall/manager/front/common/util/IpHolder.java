package com.aliyun.gts.gmall.manager.front.common.util;

public class IpHolder {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static String get() {
        return HOLDER.get();
    }

    public static void set(String ip) {
        HOLDER.set(ip);
    }
}
