package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 推送模板
 * @author yangl
 */
public enum CustCancelTemplateEnum implements GenericEnum {

    WAITING_FOR_PAYMENT(61, "switch_customer_cancel_order_cust"),
    PARTIALLY_PAID(62, "switch_customer_cancel_order_cust"),
    PAYMENT_CONFIRMED(63, "switch_customer_cancel_order_cust"),
    ACCEPTED_BY_MERCHANT(64, "switch_customer_cancel_order_cust");

    private final Integer code;

    private final String script;


    CustCancelTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static CustCancelTemplateEnum codeOf(Integer code) {
        return Arrays.stream(CustCancelTemplateEnum.values())
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
