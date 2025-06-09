package com.aliyun.gts.gmall.manager.front.login.dto.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final long MINUTE = 1000 * 60;

    public static boolean isInOneMinute(Date d1, Date d2) {

        return Math.abs(d1.getTime() - d2.getTime()) <= MINUTE;

    }
}
