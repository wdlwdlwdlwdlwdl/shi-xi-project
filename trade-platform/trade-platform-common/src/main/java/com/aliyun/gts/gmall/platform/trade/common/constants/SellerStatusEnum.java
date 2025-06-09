package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SellerStatusEnum  implements I18NEnum {

    ENABLE("1", "|normal|"),   //# "正常"
    DISABLE("0", "|removed|");  //# 清退

    // ========================

    private final String code;
    
    private final String script;


    SellerStatusEnum(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static SellerStatusEnum codeOf(String code) {
        return Arrays.stream(SellerStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }

    // 获取解析
    public String getDesc() { return getMessage(); }

}

