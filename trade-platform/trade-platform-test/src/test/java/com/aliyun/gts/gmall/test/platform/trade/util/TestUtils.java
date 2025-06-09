package com.aliyun.gts.gmall.test.platform.trade.util;

import java.lang.reflect.Field;

public class TestUtils {

    public static <T> T getPrivateField(Object o, Class clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
