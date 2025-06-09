package com.aliyun.gts.gmall.center.trade.core.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

public enum PayAccountTypeEnum  implements I18NEnum {
    PLATFORM_SPLIT_ACCOUNT(1, "|profit.account|"),   //# "平台分润账户"
    SELLER_ACCOUNT(2, "|merchant.account|");  //# 商家账户

    private int code;
    
    private String script;


    PayAccountTypeEnum(int code,  String script) {
        this.code = code;
        this.script = script;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
