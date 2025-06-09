package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 支付消息模板
 * @author yangl
 */
public enum PayTemplateEnum implements GenericEnum {

    //发邮件业务场景
    EMAIL_PAYMENT_CONFIRMED(63, "switch_payment_confirmed_seller"),
    EMAIL_PARTIALLY_PAID(62, "switch_partially_paid_seller"),
    EMAIL_CANCEL_REQUESTED(71, "switch_order_cancel_customer_performed_seller"),
    EMAIL_WAITING_FOR_ACCEPT(12, "switch_waiting_accept_seller");

    private final Integer code;

    private final String script;


    PayTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PayTemplateEnum codeOf(Integer code) {
        return Arrays.stream(PayTemplateEnum.values())
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
