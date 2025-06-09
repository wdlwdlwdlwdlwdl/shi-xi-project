package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

public enum PrimaryOrderFlagEnum implements GenericEnum {

    PRIMARY_ORDER(1, "|main.order|"),   //# "主订单"
    SUB_ORDER(0, "|sub.order|");  //# 子订单


    // ========================


    private final Integer code;
    
    private final String script;


    PrimaryOrderFlagEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static PrimaryOrderFlagEnum codeOf(Integer code) {
        return Arrays.stream(PrimaryOrderFlagEnum.values())
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
