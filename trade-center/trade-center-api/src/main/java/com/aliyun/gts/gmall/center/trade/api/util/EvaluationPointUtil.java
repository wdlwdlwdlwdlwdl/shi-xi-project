package com.aliyun.gts.gmall.center.trade.api.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yilj
 */
public class EvaluationPointUtil {

    /**
    *功能描述  提取汉字
    * @author yilj
    * @date 2022/12/21
     * @param character
    * @return
    */
    public static String getChineseCharacter(String character){
        return character.replaceAll("\\s*","").replaceAll("[^(\\u4e00-\\u9fa5)]","");
    }

    /**
     *功能描述  提取汉字
     * @author yilj
     * @date 2022/12/21
     * @param character
     * @return
     */
    public static String getEnglishCharacter(String character){
        return character.replaceAll("\\s*","").replaceAll("[^(A-Za-z)]","");
    }

    /**
     *功能描述  提取数字、汉字、字母
     * @author zhi
     * @date 2023/01/08
     * @param character
     * @return
     */
    public static String getAvailableCharacter(String character){
        return availableFilter(character).replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]","");
    }

    public static String availableFilter (String str){
        String regEx="[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 当前时间的最后一月的最后一天
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, last);
        //最后一秒
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        return cal.getTime();
    }
}
