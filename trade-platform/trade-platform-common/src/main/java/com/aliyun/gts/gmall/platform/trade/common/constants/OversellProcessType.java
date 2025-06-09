package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import java.util.Arrays;

/**
 * 超卖处理方式
 */
public enum OversellProcessType  implements I18NEnum {

    AUTO_CLOSE(1, "|auto.close|"),   //# "自动关单"
    MANUAL_WORK(2, "|manual.process|");  //# 人工处理


    // ========================


    private final Integer code;
    
    private final String script;


    OversellProcessType(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static OversellProcessType codeOf(Integer code) {
        return Arrays.stream(OversellProcessType.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }
    public String getScript() {
        return script;
    }

    // 获取解析
    public String getDesc() { return getMessage(); }
}
