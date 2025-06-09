package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import org.apache.logging.log4j.util.Strings;

import java.util.Date;

/**
 * 类型转换工具类
 *
 * @author linyi
 */
public class ObjectUtils {

    public static String toString(Object obj) {

        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public static Long toLong(Object obj) {

        if (obj == null) {
            return null;
        }

        if (Strings.isBlank(obj.toString())) {
            return null;
        }

        return Long.parseLong(obj.toString());
    }

    public static Integer toInt(Object obj) {

        if (obj == null) {
            return null;
        }

        if (Strings.isBlank(obj.toString())) {
            return null;
        }

        return Integer.parseInt(obj.toString());
    }

    public static Date toDate(Object obj) {

        if (obj == null) {
            return null;
        }

        if (obj instanceof Date) {
            return (Date)obj;
        }

        if (obj instanceof Long) {
            return new Date((Long)obj);
        }

        if (obj instanceof String) {
            return new Date(toLong(obj));
        }

        throw new IllegalArgumentException("需要timestamps字符串格式");

    }

}
