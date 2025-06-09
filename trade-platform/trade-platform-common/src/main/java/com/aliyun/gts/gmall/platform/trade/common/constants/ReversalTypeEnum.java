package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

public enum ReversalTypeEnum implements GenericEnum {

    REFUND_ONLY(1, "|refund.only|"),   //# "仅退款"
    REFUND_ITEM(2, "|return.refund|"),   //# "退货退款"
    APPLY_CANCEL_REFUND(3, "|cancellation.request.refund|"), //#"申请取消需退款"
    APPLY_CANCEL_NOT_REFUND(4, "|cancellation.request.no.refund|");//#申请取消无退款


    // ========================


    private final Integer code;
    
    private final String script;


    ReversalTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ReversalTypeEnum codeOf(Integer code) {
        return Arrays.stream(ReversalTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
