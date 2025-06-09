package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/4 13:51
 */
public class DateUtils {
    public static long oneHour = 3600000;
    public static String format1 =  "yyyyMMdd";
    public static String format2 = "yyyy-MM-dd HH:mm:ss";
    public static String format3 =  "yyyyMMdd";

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date add(Date date,int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);//增加一天
        return cal.getTime();
    }

    public static Date addMinute(Date date,int mintue) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, mintue);//增加一天
        return cal.getTime();
    }

    public static boolean isBetween(Date start, Date end,Date now){
        if(start == null || end == null){
            return false;
        }
        if(now.getTime() > start.getTime() && now.getTime() <end.getTime()){
            return true;
        }
        return false;
    }

    /**
     * a 小于 b
     * @param a
     * @param b
     * @return
     */
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
     * a 小于 b
     * @param a
     * @param b
     * @return
     */
    public static boolean isBig(Date a, Date b){
        if(a == null || b == null){
            return false;
        }
        if( a.getTime() > b.getTime()){
            return true;
        }
        return false;
    }

    /**
     * 获取当前周的周一的日期
     * @param date 传入当前日期
     * @return
     */
    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static void main(String []args){

    }
}
