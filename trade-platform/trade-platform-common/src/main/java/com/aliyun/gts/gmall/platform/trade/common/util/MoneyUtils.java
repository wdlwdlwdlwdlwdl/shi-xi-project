package com.aliyun.gts.gmall.platform.trade.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.math.BigDecimal;

public class MoneyUtils {

    public static boolean checkMoneyYuan(String yuan) {
        if (StringUtils.isBlank(yuan)) {
            return false;
        }
        if (StringUtils.isNumeric(yuan)) {
            return true;
        }
        int dot = yuan.indexOf('.');
        int dot2 = yuan.indexOf('.', dot+1);
        return dot2 < 0 && dot > 0 && dot < yuan.length() - 1 && dot >= yuan.length() - 3;
    }

    public static long yuanToCent(String yuan) {
        return new BigDecimal(yuan).multiply(BigDecimal.valueOf(100)).longValue();
    }

    public static String centToYuan(long cent) {
        return BigDecimal.valueOf(cent).divide(BigDecimal.valueOf(100)).toString();
    }

    public static void main(String[] args) {
        Assert.isTrue(checkMoneyYuan("1"));
        Assert.isTrue(checkMoneyYuan("0.1"));
        Assert.isTrue(checkMoneyYuan("0.11"));
        Assert.isTrue(!checkMoneyYuan("1."));
        Assert.isTrue(!checkMoneyYuan(".1"));
        Assert.isTrue(!checkMoneyYuan("0.111"));
        Assert.isTrue(!checkMoneyYuan("0..11"));

        Assert.isTrue(yuanToCent("0.1") == 10);
        Assert.isTrue("0.1".equals(centToYuan(10)));
    }
}
