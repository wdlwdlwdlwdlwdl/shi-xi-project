package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 短信模板
 * @author yangl
 */
public enum TimeOutTemplateEnum implements GenericEnum {

    PARTIALLY_PAID(62, "switch_order_cancel_presale_timeout_cust"),
    WAITING_FOR_ACCEPT(12, "switch_order_cancel_accept_timeout_cust"),
    WAITING_FOR_PAYMENT(61, "switch_cancel_pay_timeout_cust"),
    ACCEPTED_BY_MERCHANT(63, "switch_order_cancel_accept_timeout_cust"),
    ACCEPTED_BY_MERCHANT_COMPLETE(64, "switch_order_cancel_complete_timeout_cust"),
    DELIVERY(67, "switch_order_cancel_complete_timeout_cust"),
    READY_FOR_PICKUP(68, "switch_order_cancel_complete_timeout_cust");

    private final Integer code;

    private final String script;

    TimeOutTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static TimeOutTemplateEnum codeOf(Integer code) {
        return Arrays.stream(TimeOutTemplateEnum.values())
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

    @Override
    public String getScript() {
        return script;
    }
}
