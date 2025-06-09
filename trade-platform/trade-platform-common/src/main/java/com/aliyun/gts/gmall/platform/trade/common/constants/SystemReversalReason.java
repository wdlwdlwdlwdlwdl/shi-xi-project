package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SystemReversalReason implements GenericEnum {

    OVER_SALE(-999, "|auto.refund|"),   //# "超卖自动退款"
    SYSTEM_REFUND(-998, "|system.refund|");  //# 系统自动退款


    // ========================

    private final Integer code;
    
    private final String script;


    SystemReversalReason(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static SystemReversalReason codeOf(Integer code) {
        return Arrays.stream(SystemReversalReason.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
