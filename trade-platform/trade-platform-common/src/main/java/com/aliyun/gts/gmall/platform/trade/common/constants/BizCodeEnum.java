package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

public enum BizCodeEnum  implements I18NEnum {

    NORMAL_TRADE("NORMAL_TRADE", "|normal.trade|");  //# 普通交易


    // ========================

    private final String code;
    
    private final String script;


    BizCodeEnum(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static BizCodeEnum codeOf(String code) {
        return Arrays.stream(BizCodeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
