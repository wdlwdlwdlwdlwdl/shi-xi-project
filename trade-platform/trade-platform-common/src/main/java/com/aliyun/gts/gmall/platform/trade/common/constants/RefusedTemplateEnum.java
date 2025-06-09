package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 推送模板
 * @author yangl
 */
public enum RefusedTemplateEnum implements GenericEnum {


    CANCEL_REQUESTED(71, "switch_order_cancel_customer_refused_cust"),
    REQUESTED_REFUSED(15, "switch_refund_requested_hm_refused_cust"),
    WAITING_FOR_RETURN_REFUSED(12, "switch_waiting_accept_merchant_refused_cust");

    private final Integer code;

    private final String script;


    RefusedTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static RefusedTemplateEnum codeOf(Integer code) {
        return Arrays.stream(RefusedTemplateEnum.values())
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
