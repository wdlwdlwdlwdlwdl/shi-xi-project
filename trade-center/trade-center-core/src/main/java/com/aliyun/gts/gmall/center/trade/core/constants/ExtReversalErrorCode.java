package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ExtReversalErrorCode implements ResponseCode {

     CREATE_REVERSAL_OUT_OF_QTY("20810003", "|after.sale.combo.quantity|"),   //# "申请售后的组合商品数量超出范围"
    ;


    // =================================

    private String code;

    private String script;

    ExtReversalErrorCode(String code, String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static ExtReversalErrorCode codeOf(String code) {
        return Arrays.stream(ExtReversalErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
