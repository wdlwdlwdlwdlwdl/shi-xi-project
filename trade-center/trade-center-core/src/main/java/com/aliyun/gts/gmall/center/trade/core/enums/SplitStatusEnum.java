package com.aliyun.gts.gmall.center.trade.core.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

public enum SplitStatusEnum  implements I18NEnum {

    INITED(0, "|initializing|"),   //# "初始化"
    PROCESSING(1, "|processing|"),   //# "处理中"
    SUCCESS(2, "|success|"),   //# "成功"
    FAIL(3, "|failure|");  //# 失败

    private int code;
    
    private String script;


    SplitStatusEnum(int code,  String script) {
        this.code = code;
        this.script = script;
    }


    public int getCode() {
        return code;
    }

    @Override
    public String getScript() {
        return script;
    }

    public String getName() {
        return getMessage();
    }

}
