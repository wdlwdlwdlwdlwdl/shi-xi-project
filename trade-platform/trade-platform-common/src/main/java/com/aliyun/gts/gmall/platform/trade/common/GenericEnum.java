package com.aliyun.gts.gmall.platform.trade.common;

import com.aliyun.gts.gmall.framework.i18n.I18NEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * chaoyin
 */
public interface GenericEnum extends I18NEnum {

    /**
     * 代码
     * @return
     */
    @JsonValue
    Integer getCode();


    /**
     * 从代码获得枚举类型
     * @param clz
     * @param code
     * @param <T>
     * @return
     * @throws IllegalArgumentException
     */
    static <T extends Enum<T> & com.aliyun.gts.gmall.platform.trade.common.GenericEnum> T fromCode(Class<T> clz, int code) throws IllegalArgumentException {
        T[] enums = clz.getEnumConstants();
        for (int i = 0; i < enums.length; i++) {
            T t = enums[i];
            if (t.getCode() == code) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown generic enum");
    }

}
