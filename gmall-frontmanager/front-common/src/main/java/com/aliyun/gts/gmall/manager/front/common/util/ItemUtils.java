package com.aliyun.gts.gmall.manager.front.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

/**
 * 商品信息处理
 * 价格、重量等展示
 *
 * @author tiansong
 */
public class ItemUtils {

    private static       DecimalFormat priceFormat       = new DecimalFormat("0.##");
    private static final String        DEFAULT_PRICE_STR = "0";
    private static final BigDecimal    YUAN_UNIT         = BigDecimal.valueOf(1L);
    private static final String        PRICE_RANGE_SPLIT = "-";
    private static final BigDecimal    POINT_UNIT        = BigDecimal.valueOf(1000L);
    private static       DecimalFormat pointFormat       = new DecimalFormat("0.###");

    /**
     * 分转元
     *
     * @param money
     * @return
     */
    public static String fen2Yuan(Long money) {
        if (money == null || money <= 0L) {
            return DEFAULT_PRICE_STR;
        }
        return priceFormat.format(BigDecimal.valueOf(money).divide(YUAN_UNIT));
    }

    /**
     * 元转分
     *
     * @param money
     * @return
     */
    public static Long yuan2Fen(String money) {
        if (StringUtils.isBlank(money)) {
            return 0L;
        }
        try {
            return new BigDecimal(money).multiply(YUAN_UNIT).longValue();
        } catch (Exception e) {
        }
        return 0L;
    }

    /**
     * 价格区间的展示方式
     *
     * @param minPrice
     * @param maxPrice
     * @return
     */
    public static String fen2YuanRange(Long minPrice, Long maxPrice) {
        if (minPrice == null || maxPrice == null || minPrice <= 0L || maxPrice < minPrice) {
            return DEFAULT_PRICE_STR;
        }
        if (minPrice.equals(maxPrice)) {
            return String.valueOf(minPrice);
        }
        return String.valueOf(minPrice) + PRICE_RANGE_SPLIT + String.valueOf(maxPrice);
    }

    private static final Integer MAX_DISCOUNT = 100;

    /**
     * 计算折扣价
     *
     * @param money
     * @param discount
     * @return
     */
    public static Long discountPrice(Long money, Integer discount) {
        if (money == null || money <= 0L || discount >= MAX_DISCOUNT || discount <= 0) {
            return 0L;
        }
        return BigDecimal.valueOf(money).multiply(BigDecimal.valueOf(discount)).divide(YUAN_UNIT,
            BigDecimal.ROUND_HALF_UP).longValue();
    }

    private static       DecimalFormat weightFormat = new DecimalFormat("0.###");
    private static final Double        weightUnit   = 1000.0;
    private static final String        gUnit        = "g";
    private static final String        kgUnit       = "kg";

    /**
     * 重量展示规则
     *
     * @param weight
     * @param withUnit
     * @return
     */
    public static String showWeight(Long weight, Boolean withUnit) {
        if (weight == null || weight <= 0L) {
            return null;
        }
        if (weight < weightUnit) {
            return withUnit ? weight + gUnit : weight.toString();
        }
        String weightKg = weightFormat.format(weight.doubleValue() / weightUnit);
        return withUnit ? weightKg + kgUnit : weightKg;
    }

    public static String pointDisplay(Long pointCount) {
        if (pointCount == null || pointCount == 0L) {
            return DEFAULT_PRICE_STR;
        }
        // 积分支持负数
        return pointFormat.format(BigDecimal.valueOf(pointCount).divide(POINT_UNIT));
    }

    public static Long pointInput(String pointInput) {
        if (StringUtils.isBlank(pointInput)) {
            return 0L;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(pointInput);
            if (bigDecimal.longValue() <= 0L) {
                return 0L;
            }
            return bigDecimal.multiply(POINT_UNIT).longValue();
        } catch (Exception e) {
        }
        return 0L;
    }

    public static long nullZero(Long value) {
        return value == null ? 0L : value.longValue();
    }

    public static void main(String[] args) {
        System.out.println(">>>>>>>>>>>>fen2Yuan test");
        System.out.println(fen2Yuan(-1L));
        System.out.println(fen2Yuan(0L));
        System.out.println(fen2Yuan(1L));
        System.out.println(fen2Yuan(11L));
        System.out.println(fen2Yuan(111L));
        System.out.println(fen2Yuan(110L));
        System.out.println(fen2Yuan(100L));
        System.out.println(fen2Yuan(9999999999999L));
        System.out.println(">>>>>>>>>>>yuan2Fen test");
        System.out.println(yuan2Fen(null));
        System.out.println(yuan2Fen("0L"));
        System.out.println(yuan2Fen("0.001"));
        System.out.println(yuan2Fen("0.01"));
        System.out.println(yuan2Fen("9.999"));
        System.out.println(yuan2Fen("599.56"));
        System.out.println(yuan2Fen("99999999999.99"));

        // g2kg
        System.out.println(">>>>>>>>>>>>>>>>>g2Kg test");
        System.out.println(showWeight(-1L, Boolean.TRUE));
        System.out.println(showWeight(1L, Boolean.TRUE));
        System.out.println(showWeight(111L, Boolean.TRUE));
        System.out.println(showWeight(1111L, Boolean.TRUE));
        System.out.println(showWeight(1000L, Boolean.TRUE));
        System.out.println(showWeight(999999999999L, Boolean.TRUE));

        System.out.println(">>>>>>>>>>>>>>>>discountPrice");
        System.out.println(discountPrice(0L, 99));
        System.out.println(discountPrice(10000L, 100));
        System.out.println(discountPrice(10000L, 99));
        System.out.println(discountPrice(99999L, 99));
        System.out.println(discountPrice(11111L, 99));

        System.out.println(">>>>>>>>>>>>>>>>fen2YuanRange");
        System.out.println(fen2YuanRange(0L, 100L));
        System.out.println(fen2YuanRange(1000L, 100L));
        System.out.println(fen2YuanRange(1000L, 1000L));
        System.out.println(fen2YuanRange(1000L, 5000L));
    }
}