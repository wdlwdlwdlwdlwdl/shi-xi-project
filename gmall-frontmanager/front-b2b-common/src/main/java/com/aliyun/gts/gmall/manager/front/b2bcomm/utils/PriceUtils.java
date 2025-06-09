package com.aliyun.gts.gmall.manager.front.b2bcomm.utils; /**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/3/2 10:39
 */

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 商品价格展示转换
 *
 * @author tiansong
 */
public class PriceUtils {
    public static Long yuanToFen(String money) {
        if (StringUtils.isBlank(money)) {
            return 0L;
        }
        BigDecimal d1 = new BigDecimal(money);
        BigDecimal d2 = BigDecimal.valueOf(100d);
        BigDecimal d3 = d1.multiply(d2);
        return d3.longValue();
    }

    /**
     * 展示
     *
     * @param money
     * @return
     */
    public static String fenToYuan(Long money) {
        if (money == null) {
            return null;
        }
        return new DecimalFormat("0.00").format(money / 100.0);
    }
}