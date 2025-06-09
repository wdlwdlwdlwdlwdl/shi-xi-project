package com.aliyun.gts.gmall.manager.front.common.util;

import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import org.apache.logging.log4j.util.Strings;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author haibin.xhb
 * @date 2021/3/26 17:51
 */
public class DateUtils {
    /**
     * 当前时间
     *
     * @param now   比较的时间
     * @param start 开始时间
     * @param end   结束时间
     * @return
     */
    public static boolean isBetween(Date now, Date start, Date end) {
        if (now == null || start == null || end == null) {
            return false;
        }
        return now.getTime() > start.getTime() && now.getTime() < end.getTime();
    }


    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String formatYMD(Date date) {
        if (date == null) {
            return Strings.EMPTY;
        }
        return DateTimeUtils.format(date, formatter);
    }

    public static boolean small(Date a, Date b){
        if(a == null || b == null){
            return false;
        }
        if( a.getTime() < b.getTime()){
            return true;
        }
        return false;
    }

    /**
     * 活动结束
     * @return
     */
    public static boolean isBigThen(Date a, Date b){
        if(a == null || b == null){
            return false;
        }
        if( a.getTime() >= b.getTime()){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("false = " + isBetween(new Date(), null, null));
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        Date start = calendar.getTime();
        calendar.add(Calendar.HOUR_OF_DAY, 10);
        Date end = calendar.getTime();
        System.out.println("true = " + isBetween(now, start, end));
        System.out.println("false = " + isBetween(now, now, end));

        System.out.println(formatYMD(new Date()));
    }
}