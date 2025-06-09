package com.aliyun.gts.gmall.platform.trade.common.constants;


import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import java.util.Arrays;

/**
 * WMS页面不支持多语言，需要在url里面添加一个参数，seller，purchaseOrder
 */
public enum LanguageEnum {
    //CN 中文   EN 英文  KK 哈萨克语  RU 俄语
    //枚举值作为参数
    EN(1, "EN"),
    CN(2, "CN"),
    KK(3,"KK"),
    RU(4,"RU");


    // ========================


    private final Integer code;

    private final String script;


    LanguageEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static LanguageEnum codeOf(Integer code) {
        return Arrays.stream(LanguageEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

//    public String getName(){
//        return getMessage();
//    }
    public String getScript() {
        return script;
    }
}
