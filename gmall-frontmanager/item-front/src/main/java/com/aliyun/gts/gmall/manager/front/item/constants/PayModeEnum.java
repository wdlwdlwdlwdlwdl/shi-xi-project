package com.aliyun.gts.gmall.manager.front.item.constants;

import java.util.Arrays;

public enum PayModeEnum {

    INSTALLMENT("01", "installment"),
    /**
     * top 1数据
     */
    LOAN("02", "loan"),
    /**
     * top 2数据
     */
    EAPY("03", "epay"),


    ;

    private String code;

    private String script;


    PayModeEnum(String code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PayModeEnum codeOf(String code) {
        return Arrays.stream(PayModeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }
    public String getScript() {
        return script;
    }



}
