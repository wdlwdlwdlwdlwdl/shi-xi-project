package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;


public enum SplitStatusEnum  implements I18NEnum {

    INITED(0, "|initialization|"),   //# "初始化"
    PROCESSING(1, "|processing|"),   //# "处理中"
    SUCCESS(2, "|success|"),   //# "成功"
    FAIL(3, "|failure|");  //# 失败

    private final int code;
    
    private final String script;


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
