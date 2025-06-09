package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 推送模板
 * @author yangl
 */
public enum PushTemplateEnum implements GenericEnum {

    WAITING_FOR_PAYMENT(61, "switch_order_cancel_epay_failed_cust"),
    PARTIALLY_WAITING_FOR_PAYMENT(91, "switch_cancel_presale_pay_failed_cust"),
    PARTIALLY_PAID(62, "switch_partially_paid_cust"),
    PAYMENT_CONFIRMED(63, "switch_payment_confirmed_cust"),
    ACCEPTED_BY_MERCHANT(64, "switch_accepted_merchant_cust"),
    WAITING_FOR_COURIER(66, "switch_waiting_courier_cust"),
    DELIVERY(67, "switch_delivery_cust"),
    READY_FOR_PICKUP(68, "switch_ready_pickup_cust"),
    COMPLETED(69, "switch_completed_cust"),
    CANCELLED(72, "switch_cancelled_cust"),
    CANCEL_FAILED(73, "switch_cancel_failed_cust"),
    //售后流程
    WAITING_FOR_ACCEPT(12, "switch_waiting_accept_cust"),
    WAITING_FOR_RETURN(13, "switch_waiting_return_cust"),
    RETURNING_TO_MERCHANT(70, "switch_returning_merchant_cust"),
    WAITING_FOR_REFUND(14, "switch_waiting_refund_cust"),
    REFUND_FAILED(17, "switch_refund_failed_cust"),
    REFUND_FULL_SUCCESS(18, "switch_refund_success_cust"),
    REFUND_PART_SUCCESS(19, "switch_refund_success_cust");

    private final Integer code;

    private final String script;


    PushTemplateEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PushTemplateEnum codeOf(Integer code) {
        return Arrays.stream(PushTemplateEnum.values())
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
