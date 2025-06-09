package com.aliyun.gts.gmall.center.trade.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final long ON_DAY_MILLIS = 24 * 3600 * 1000;

    private static final ThreadLocal<DateFormat> DATETIME = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static long toDayEnd(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTimeInMillis();
    }

    public static Date parseDateTime(String dateTime) {
        try {
            return DATETIME.get().parse(dateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toDateString(Date date) {
        return DATETIME.get().format(date);
    }
}
