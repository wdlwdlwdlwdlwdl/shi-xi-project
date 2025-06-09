package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderInvoiceErrorCode implements ResponseCode {

    NO_MAIN_ORDER("20880001", "|main.order.not.found|"),   //# "未找到主订单信息"

    NO_ORDER_CONFIRM("20880010", "|order.status.not.confirmed|"),   //# "订单状态未确认收货"
    HAS_NORMAL_INVOICE("20880008", "|invoice.requested|，|no.duplicate.invoice.request|"),   //# "已经申请普通发票，请勿申请"
    HAS_SPECIAL_INVOICE("20880007", "|no.duplicate.invoice.request|，请勿申请"),   //# "已经申请专票
    NO_INVOICE_TITLE("20880003", "|invoice.head.not.found|"),   //# "发票抬头不存在"

    NO_CAN_REJECT("20880010", "|invalid.invoice.type|"),   //# "操作发票类型不符合"
    NO_ORDER_INVOICE("20880004", "|invoice.record.not.found|"),   //# "当前发票记录不存在"
    NOT_ALLOWED_OPERATION("20880005", "|invoice.voiding|，|no.duplicate.submission|！"),   //# "当前发票正在作废中，请勿重复提交

    NOT_ALLOWED_REJECT("20880009", "|invoice.voided|，|operation.not.allowed|"),   //# "当前发票已作废，不允许操作"
    REMOTE_REQUEST_FAILED("20880006", "|ticket.center.request.fail|, |try.later|"), //# 请求票务中心失败", 请稍后重试"
    NO_SUB_ORDER("20880011", "|all.after.sales.complete|，|no.invoice|"),   //# "当前订单已全部完成售后服务，无法申请发票"
    NO_CAN_APPLY("20880012", "|no.invoice.for.promo|，|no.invoice|"),   //# "当前订单商品为活动商品，无法申请发票"
    CLOSE_APPLY_INVOICE("20880013", "|invoice.entry.closed|，|no.invoice|！"),   //# "当前订单已关闭开票入口，无法申请发票
    ;


    // =================================

    private String code;

    private String script;

    OrderInvoiceErrorCode(String code, String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static OrderInvoiceErrorCode codeOf(String code) {
        return Arrays.stream(OrderInvoiceErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
