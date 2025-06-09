package com.aliyun.gts.gmall.platform.trade.common.constants;

import lombok.Getter;

import java.util.Arrays;

/**
 * 取消原因枚举
 */
@Getter
public enum CancelCodeEnum {


    OUT_OF_STOCK(206, "|OUT_OF_STOCK|");

    private final Integer code;

    private final String script;


    CancelCodeEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static CancelCodeEnum codeOf(Integer code) {
        return Arrays.stream(CancelCodeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

    public String getScript() {
        return script;
    }

}
