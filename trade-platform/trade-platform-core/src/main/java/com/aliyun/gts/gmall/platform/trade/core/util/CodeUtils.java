package com.aliyun.gts.gmall.platform.trade.core.util;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/29 14:57
 */
public final class CodeUtils {
    /**
     * 得到32位唯一的UUID
     *
     * @return 唯一编号
     */
    public static Serializable uuid() {
        UUID uid = UUID.randomUUID();
        return uid.toString().replace("-", "");
    }

    /**
     * 得到纯数字编号
     *
     * @param length
     * 长度
     * @return
     */
    public static String number(int length) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i == 0)
                str.append(getRandom(49, 57));
            else
                str.append(getRandom(48, 57));
        }
        return str.toString();
    }

    /**
     * 根据开始和结束大小得到单一字符
     * @param begin
     * 开始值
     * @param end
     * 结束值
     * @return 单一字符
     */
    private static String getRandom(int begin, int end) {
        String str = "";
        Random rd = new Random();
        int number = 0;
        while (str.length() == 0) {
            number = rd.nextInt(end + 1);
            if (number >= begin && number <= end)
                str = String.valueOf((char) number);
        }
        return str;
    }
}
