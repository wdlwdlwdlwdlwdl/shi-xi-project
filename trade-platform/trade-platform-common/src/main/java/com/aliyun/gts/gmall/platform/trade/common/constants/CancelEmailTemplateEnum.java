package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 取消邮件模板
 * @author yangl
 */
public enum CancelEmailTemplateEnum implements GenericEnum {

    //发邮件业务场景
    EMAIL_WAITING_FOR_PAYMENT(61, "switch_order_cancel_customer_performed_seller"),
    EMAIL_PAYMENT_CONFIRMED(63, "switch_order_cancel_customer_performed_seller"),
    EMAIL_PARTIALLY_PAID(62, "switch_order_cancel_customer_performed_seller"),
    EMAIL_ACCEPTED_BY_MERCHANT(64, "switch_order_cancel_customer_performed_seller");

    private final Integer code;

    private final String script;


    CancelEmailTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static CancelEmailTemplateEnum codeOf(Integer code) {
        return Arrays.stream(CancelEmailTemplateEnum.values())
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
