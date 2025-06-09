package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 推送模板
 * @author yangl
 */
public enum MerchantCancelTemplateEnum implements GenericEnum {

    WAITING_FOR_PAYMENT(61, "switch_order_cancel_merchant_cancel_cust"),
    PAYMENT_CONFIRMED(63, "switch_order_cancel_merchant_cancel_cust"),
    ACCEPTED_BY_MERCHANT(64, "switch_order_cancel_merchant_cancel_cust");

    private final Integer code;

    private final String script;


    MerchantCancelTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static MerchantCancelTemplateEnum codeOf(Integer code) {
        return Arrays.stream(MerchantCancelTemplateEnum.values())
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
