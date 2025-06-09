package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderEvaluateEnum implements GenericEnum {

    NOT_EVALUATE(0, "|not.reviewed|"),   //# "未评价"
    FIRST_EVALUATED(1, "|reviewed|"),   //# "已评价"
    ADDITIONAL_EVALUATED(2, "|reviewed.added|");  //# 已追评


    // ========================


    private final Integer code;
    
    private final String script;


    OrderEvaluateEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static OrderEvaluateEnum codeOf(Integer code) {
        return Arrays.stream(OrderEvaluateEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public static boolean isEvaluated(Integer code) {
        return FIRST_EVALUATED.code.equals(code) || ADDITIONAL_EVALUATED.code.equals(code);
    }
    public String getScript() {
        return script;
    }
}
