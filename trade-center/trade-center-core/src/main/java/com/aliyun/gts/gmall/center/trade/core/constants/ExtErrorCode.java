package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ExtErrorCode implements ResponseCode {

    ORDER_WITH_MIX_ITEM("20821001", "|merging.orders.not.supported|",  0);  //# "不支持合并下单"
    ;


    // =================================

    String code;

    String script;

    int args;

    ExtErrorCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }

    public static ExtErrorCode codeOf(String code) {
        return Arrays.stream(ExtErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
