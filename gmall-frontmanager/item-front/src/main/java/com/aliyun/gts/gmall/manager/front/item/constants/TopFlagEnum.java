package com.aliyun.gts.gmall.manager.front.item.constants;


import java.util.Arrays;

/**
 * 商家top数据
 */
public enum TopFlagEnum {

    /**
     * top 1数据
     */
    TOP_ONE("01", "TOP_ONE"),
    /**
     * top 2数据
     */
    TOP_TWO("02", "TOP_TWO");

    private String code;

    private String script;


    TopFlagEnum(String code, String script) {
        this.code = code;
        this.script = script;
    }

    public static TopFlagEnum codeOf(String code) {
        return Arrays.stream(TopFlagEnum.values()).filter(en -> en.code.equals(code)).findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }

    public String getScript() {
        return script;
    }



}
