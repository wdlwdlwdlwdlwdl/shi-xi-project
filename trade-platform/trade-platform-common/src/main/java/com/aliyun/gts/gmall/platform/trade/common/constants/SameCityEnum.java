package com.aliyun.gts.gmall.platform.trade.common.constants;

import lombok.Getter;

/**
 *是否同城
 */
@Getter
public enum SameCityEnum {
    IS_SAME(1),  //同城
    NOT_SAME(2); //非同城

    private final Integer code;

    SameCityEnum(Integer code) {
        this.code = code;
    }

    public static Integer getCityEnum(Boolean isCity) {
        return Boolean.TRUE.equals(isCity) ? IS_SAME.getCode() : NOT_SAME.getCode();
    }

}
