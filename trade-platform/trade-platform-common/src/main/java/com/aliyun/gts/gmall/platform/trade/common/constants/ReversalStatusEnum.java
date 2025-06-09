package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

public enum ReversalStatusEnum implements GenericEnum {

    WAIT_SELLER_AGREE(1, "|pending.agree|"),   //# "待卖家同意"

    // 退货
    WAIT_DELIVERY(10, "|pending.return|"),   //# "待退回发货"
    WAIT_CONFIRM_RECEIVE(11, "|pending.receipt|"),   //# "待确认收货"

    // 退款
    WAIT_REFUND(20, "|refunding|"),   //# "退款中"

    // 终态
    REVERSAL_OK(100, "|aftersale.complete|"),   //# "售后完成"
    CUSTOMER_CLOSE(101, "|buyer.cancel|"),   //# "买家取消"
    SELLER_REFUSE(102, "|seller.reject|"),  //# 卖家拒绝
    //halyk逆向售后状态码
    WAITING_FOR_ACCEPT(12, "|waiting.for.accpet|"),// 等待接受
    WAITING_FOR_RETURN(13, "|waiting.for.return|"),// 等待退回
    WAITING_FOR_REFUND(14, "|waiting.for.refund|"),// 等待退款
    REFUND_REQUESTED(15, "|refund.rquested|"),// 退款已请求
    REFUND_APPROVED(16, "|refund.approved|"),// 退款审批
    REFUND_FAILED(17, "|refund.failed|"),// 退款失败
    REFUND_FULL_SUCCESS(18, "|refund.full.success|"),// 退款成功
    REFUND_PART_SUCCESS(19, "|refund.part.success|"),// 退款部分成功
    COMPLETED(69, "|completed|"),
    CANCEL_REQUESTED(71, "|cancel.requested|"), // 已请求取消订单
    CANCEL_FAILED(73, "|cancel.failed|"),
    CANCELLED(72, "|cancelled|"), ;
    // ========================


    private final Integer code;
    
    private final String script;


    ReversalStatusEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ReversalStatusEnum codeOf(Integer code) {
        return Arrays.stream(ReversalStatusEnum.values())
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
