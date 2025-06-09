package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 支付状态
 */
public enum PayStatusEnum implements GenericEnum {

    PAY_PAID(1, "|pay.paid|"),
    PAY_UNPAID(0, "|pay.unpaid|"),
    PAY_ERROR(2, "|pay.failed|");


    // ========================

    private final Integer code;

    private final String script;


    PayStatusEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PayStatusEnum codeOf(Integer code) {
        return Arrays.stream(PayStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
