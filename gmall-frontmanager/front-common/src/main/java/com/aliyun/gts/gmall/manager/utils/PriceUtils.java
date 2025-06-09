package com.aliyun.gts.gmall.manager.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 商品价格展示转换
 *
 * @author tiansong
 */
public class PriceUtils {

    public static Double fenToYuan(Long money) {
        if (money != null) {
            Double value = money * 1.0 / 100;
            return value;
        }
        return null;
    }

    public static Long yuanToFen(String money) {
        if (StringUtils.isBlank(money)) {
            return 0L;
        }
        Double d = new Double(money) * 100;
        return d.longValue();
    }

    public static Double fenToYuan(String money) {
        if (StringUtils.isBlank(money)) {
            return null;
        }
        Long value = NumberUtils.toLong(money);
        return fenToYuan(value);
    }

    public static String fenToYuanStr(String money) {
        if (StringUtils.isBlank(money)) {
            return null;
        }
        return fenToYuan(money).toString();
    }

    public static String fenToYuanStr(Long money) {
        if (money == null) {
            return null;
        }
        return new DecimalFormat("0.00").format(money / 100.0);
    }

    /**
     * 末尾没有0
     *
     * @param money
     * @return
     */
    public static String fenToYuanZ(Long money) {
        if (money == null) {
            return null;
        }
        return new DecimalFormat("0.##").format(money / 100.0);
    }

    /**
     * 折扣展示,删除末尾的0
     *
     * @param disc
     * @return
     */
    public static String toZhe(Integer disc) {
        int v = disc;
        while (v % 10 == 0) {
            v = v / 10;
            if (v == 0) {
                break;
            }
        }
        return v + "";
    }

    public static Long yuan2Fen(Double money) {
        if (money == null) {
            return null;
        }
        BigDecimal d1 = BigDecimal.valueOf(money);
        BigDecimal d2 = BigDecimal.valueOf(100d);
        BigDecimal d3 = d1.multiply(d2);
        return d3.toBigInteger().longValue();
    }

    /**
     * 比例
     *
     * @param discount 百分比
     * @param money
     * @return
     */
    public static Long discountPrice(Integer discount, Long money) {
        if (money == null) {
            return null;
        }
        BigDecimal d1 = BigDecimal.valueOf(money);
        BigDecimal d2 = BigDecimal.valueOf(discount);
        BigDecimal d100 = BigDecimal.valueOf(100);
        BigDecimal d3 = d2.divide(d100).multiply(d1);
        return d3.toBigInteger().longValue();
    }

    /**
     * 折后
     *
     * @param discount 比例 小数点
     * @param money
     * @return
     */
    public static Long discountPrice(Double discount, Long money) {
        if (money == null) {
            return null;
        }
        BigDecimal d1 = BigDecimal.valueOf(money);
        BigDecimal d2 = BigDecimal.valueOf(discount);
        BigDecimal d3 = d1.multiply(d2);
        return d3.toBigInteger().longValue();
    }

}