package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

public enum OrderStatusEnum implements GenericEnum {

    // 支付
    ORDER_WAIT_PAY(10, "|waiting.payment|"),   //# "等待支付"
    @Deprecated
    ORDER_PAIED(11, "完成支付"),   // 没有使用

    // 履约中
    ORDER_WAIT_DELIVERY(9, "|pending.shipment|"),   //# "待发货"
    ORDER_SENDED(20, "|shipped|"),   //# "已发货"
    STEP_ORDER_DOING(21, "|multi.stage|"),   //# "多阶段进行中"


    // 🀄️终态

    ORDER_CONFIRM(25, "|confirm.receipt|"),   //# "确认收货"
    SYSTEM_CONFIRM(27, "|system.receipt|"),   //# "系统确认收货"
    @Deprecated
    ORDER_SUCCESS(30, "交易成功"),   // 没有使用

    ORDER_BUYER_CANCEL(33, "|buyer.cancel|"),   //# "买家取消"
    ORDER_SELLER_CLOSE(34, "|seller.close|"),   //# "卖家关闭"
    REVERSAL_SUCCESS(35, "|aftersale.complete|"),   //# "售后完成"

    @Deprecated
    REVERSAL_CLOSE(36, "售后关闭"),   // 没有使用

    SYSTEM_CLOSE(37, "|system.close|"),   //# "系统关闭"


    // 🀄️售后

    REVERSAL_DOING(40, "|aftersale.process|"),   //# "售后进行中"

    WAIT_SELLER_CONFIRM(50, "|waiting.confirm|"),   //# "等待卖家确认接单"

    WAIT_SELLER_RECEIVED(51, "|waiting.pay.confirm|"),   //# "等待卖家确认收款"
    WAIT_BUYER_TRANSFERED(52, "|waiting.buyer.confirm|"),   //# "等待买家确认打款"

//    SELLER_CONFIRMED(53, "\"商家已确认|",  //商家确认后，对公支付为商家确认应收单之后，账期支付为商家确认接单后
    SELLER_CANCELING(54, "|cancellation.pending|"), //#"取消中"),
    SELLER_AGREE_CANCEL(55, "|merchant.agreed.cancellation|"), //#"商家同意取消"),;
    
    /**
     * halyk 商城的订单状态继续添加原有状态不变
     * @param status
     * @return
     */
    CREATED(60, "|order.created|"), // 订单创建

    PAYMENT_CONFIRMING(59, "|payment.confirming|"),//支付中

    WAITING_FOR_PAYMENT(61, "|waiting.for.payment|"), // 等待支付

    PARTIALLY_PAID(62, "|partially.paid|"), // 定金支付

    PAYMENT_CONFIRMED(63, "|payment.confirmed|"), // 确认支付

    ACCEPTED_BY_MERCHANT(64, "|accept.by.merchant|"), // 商家确认订单

    DELIVERY_TO_DC(65, "|delivery.to.dc|"), // 订单被移交至配送中心

    WAITING_FOR_COURIER(66, "|waiting.for.courier|"), // 等待快递送货

    DELIVERY(67, "|delivery|"), // 商品已被移交给 用户 PVZ 或 Postamat

    READY_FOR_PICKUP(68, "|ready.for.pick.up|"), // 准备提货

    COMPLETED(69, "|completed|"), // 订单完成 商品接受并交到用户手中

    RETURNING_TO_MERCHANT(70, "|returning.to.merchant|"), //拒收

    CANCEL_REQUESTED(71, "|cancel.requested|"), // 已请求取消订单

    CANCELLED(72, "|cancelled|"), // 订单已被取消

    CANCEL_FAILED(73, "|cancel.failed|"),// 订单取消失败

    //售后流程
    WAITING_FOR_ACCEPT(12, "|waiting.for.accpet|"),// 等待接受

    WAITING_FOR_RETURN(13, "|waiting.for.return|"),// 等待退回

    WAITING_FOR_REFUND(14, "|waiting.for.refund|"),// 等待退款

    REFUND_REQUESTED(15, "|refund.rquested|"),// 退款已请求

    REFUND_APPROVED(16, "|refund.approved|"),// 退款审批

    REFUND_FAILED(17, "|refund.failed|"),// 退款失败

    REFUND_FULL_SUCCESS(18, "|refund.full.success|"),// 退款成功

    REFUND_PART_SUCCESS(19, "|refund.part.success|");

    // ========================

    // 是否售后相关的状态
    public static boolean isReversal(Integer status) {
        OrderStatusEnum s = OrderStatusEnum.codeOf(status);
        return s == REFUND_REQUESTED
            || s == WAITING_FOR_ACCEPT
            || s == WAITING_FOR_REFUND
            || s == REFUND_APPROVED
            || s == REFUND_FULL_SUCCESS
            || s == REFUND_PART_SUCCESS
            || s == WAITING_FOR_RETURN;
    }


    // ========================


    private final Integer code;
    
    private final String script;


    OrderStatusEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static OrderStatusEnum codeOf(Integer code) {
        return Arrays.stream(OrderStatusEnum.values())
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
