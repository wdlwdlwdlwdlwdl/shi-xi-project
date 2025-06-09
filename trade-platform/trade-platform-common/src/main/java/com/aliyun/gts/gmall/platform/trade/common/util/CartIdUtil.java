package com.aliyun.gts.gmall.platform.trade.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CartIdUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static String convert(String custId) {
        String date = getDateFormat();
        String custSplit = getCustSplit(custId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(date).append(System.currentTimeMillis()%1000).append(custSplit);
        return stringBuilder.toString();
    }

    private static String getDateFormat() {
        return LocalDateTime.now().format(DATE_FORMAT);
    }


    private static String getCustSplit(String custId) {
        return RouterIdUtils.getRouterId(custId);
    }
}
