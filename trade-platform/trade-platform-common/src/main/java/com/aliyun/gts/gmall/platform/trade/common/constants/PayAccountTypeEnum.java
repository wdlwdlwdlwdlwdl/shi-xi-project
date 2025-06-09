package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

public enum PayAccountTypeEnum  implements I18NEnum {
    PLATFORM_SPLIT_ACCOUNT(1, "|platform.account|"),   //# "平台分润账户"
    SELLER_ACCOUNT(2, "|merchant.account|");  //# 商家账户


    private final int code;
    
    private final String script;


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

    // 获取解析
    public String getDesc() { return getInner(); }

}
