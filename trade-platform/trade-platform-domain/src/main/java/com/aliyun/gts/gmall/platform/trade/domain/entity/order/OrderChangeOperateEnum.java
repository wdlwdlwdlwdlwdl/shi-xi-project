package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import lombok.Getter;

@Getter
public enum OrderChangeOperateEnum implements OrderChangeOperate {

    CUST_CREATE_ORDER(CUSTOMER, "|create.order|",  false),  //# "创建订单"
    CUST_PAY(CUSTOMER, "|place.payment|",  false),  //# "下单支付"

//    CUST_CANCEL(CUSTOMER, "|cancel.order|",  false),  //# "取消订单"
    CUST_CANCEL(CUSTOMER, I18NMessageUtils.getMessage("cancel.order"),  false),  //# "取消订单"

    CUST_CONFIRM_RECEIVE(CUSTOMER, "|confirm.receipt|",  true),  //# "确认收货"
    SELLER_SEND(SELLER, "|ship|",  false),  //# "发货"
    SELLER_CLOSE(SELLER, "|close.order|",  false),  //# "关闭订单"
    SELLER_CHANGE_FEE(SELLER, "|change.price|",  false),  //# "改价"

    //halyk新状态
    SELLER_CANCEL(SELLER, "|close.order|",  false),

    DELIVERY_UPDATE(SYSTEM, "|delivery.update|",  false),

    SYS_CONFIRM_RECEIVE(SYSTEM, "|confirm.receipt|",  true),  //# "确认收货"

//    SYS_CLOSE(SYSTEM, "|close.order|",  false),  //# "关闭订单"
    SYS_CLOSE(SYSTEM, I18NMessageUtils.getMessage("close.order"),  false),  //# "关闭订单"
//    SYS_CANCEL(SYSTEM, "|close.order|",  false),  //# "取消订单"
    SYS_CANCEL(SYSTEM, I18NMessageUtils.getMessage("cancel.requested"),  false),  //# "取消订单"

    SYS_CONFIRM_BY_REVERSAL(SYSTEM, "|complete.end|",  true),  //# "售后完成订单完结"

    // 多阶段
    STEP_CUST_PAY(CUSTOMER, I18NMessageUtils.getMessage("partially.paid"),  false),  //# "多阶段支付"
    STEP_SELLER_HANDLE(SELLER, "|multi.seller|",  false),  //# "多阶段卖家处理"
    STEP_CUST_CONFIRM(CUSTOMER, "|multi.user|",  false),  //# "多阶段用户确认"
    DELIVERY(SYSTEM, I18NMessageUtils.getMessage("delivery"),  false),
    READY_FOR_PICKUP(SYSTEM, I18NMessageUtils.getMessage("ready.for.pick.up"),  false),
    RETURNING_TO_MERCHANT(SYSTEM, I18NMessageUtils.getMessage("returning.to.merchant"),  false),
    CANCEL_REQUESTED(SYSTEM, I18NMessageUtils.getMessage("cancel.requested"),  false),
    CANCELLED(SYSTEM, I18NMessageUtils.getMessage("cancelled"),  false),
    CANCEL_FAILED(SYSTEM, I18NMessageUtils.getMessage("cancel.failed"),  false),
    COMPLETED(SELLER, I18NMessageUtils.getMessage("completed"),  true),
    CUST_PAY_COMFIRMING(CUSTOMER, "|payment.confirming|",  false),
    CUST_PAY_COMFIRM(CUSTOMER, "|payment.confirmed|",  false),
    WAITING_FOR_PAYMENT(CUSTOMER, "|waiting.for.payment|",  false),
    ACCEPTED_BY_MERCHANT(SELLER, I18NMessageUtils.getMessage("accept.by.merchant"),  false);
    // ===================

    private final String script;
    private final int oprType;
    private String opName;
    private final boolean orderSuccess;    // 是否订单完结, 即进行打款、发放积分的步骤

    OrderChangeOperateEnum(int oprType, String script,  boolean orderSuccess) {
        this.oprType = oprType;
        this.script = script;
        this.orderSuccess = orderSuccess;
    }
    public String getScript() {
        return script;
    }

    public String getOpName() {
        return getMessage();
    }
}
