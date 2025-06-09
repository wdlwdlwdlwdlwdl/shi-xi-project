package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 支付方式
 */
public enum PayTypeEnum implements GenericEnum {

    EPAY(108, "|pay.card|"),
    LOAN(109, "|pay.loan|"),
    INSTALLMENT(110, "|pay.installment|"),
    LOAN_INSTALLMENT(111, "|Loan/Installment|");//取消配置两种和一起


    // ========================

    private final Integer code;

    private final String script;


    PayTypeEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PayTypeEnum codeOf(Integer code) {
        return Arrays.stream(PayTypeEnum.values())
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
