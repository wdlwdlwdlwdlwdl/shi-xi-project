package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 短信模板
 * @author yangl
 */
public enum SmsTemplateEnum implements GenericEnum {

    SMS_CANCEL_FAILED(73, "switch_cancel_failed_cust"),
    SMS_SEND(64, "send_sms_verification_code"),// 商家接单发货后发送OTP
    SMS_PAYMENT_CONFIRMED(63, "switch_payment_confirmed_seller");//支付成功通知商家

    private final Integer code;

    private final String script;

    SmsTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static SmsTemplateEnum codeOf(Integer code) {
        return Arrays.stream(SmsTemplateEnum.values())
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
