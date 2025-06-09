package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

import java.util.Arrays;

public enum EvoucherErrorCode implements ResponseCode {

    // 下单、发码相关

    ORDER_WITH_MIX_ITEM("20810001", "|voucher.and.goods|"),   //# "电子凭证与普通商品不能一起下单"
    ORDER_QTY_OUT_LIMIT("20810002", "|voucher.order.limit|"),   //# "电子凭证下单数量超出限制"
    ORDER_ITEM_MISS_EV_INFO("20810003", "|missing.voucher.info|"),   //# "商品缺少电子凭证信息"
    ORDER_MISS_EV_TEMPLATE("20810004", "|no.voucher.template|"),   //# "订单无电子凭证模版"
    ORDER_EV_TEMPLATE_ERROR("20810005", "|voucher.template.error|"),   //# "电子凭证模版内容异常"
    REPEAT_SEND_EV("20810006", "|duplicate.voucher.issue|"),   //# "电子凭证重复发放"


    // 核销相关

    EV_NOT_EXIST("20810101", "|voucher.not.exist|"),   //# "电子凭证不存在"
    STATUS_ILLEGAL("20810102", "|invalid.voucher.status|"),   //# "电子凭证状态无效"
    ALREADY_WRITE_OFF("20810103", "|voucher.redeemed|, |no.repeated.redemption|"), //# 电子凭证已被核销", 无法重复核销"
    TIME_NOT_AVAILABLE("20810104", "|voucher.not.in.validity|"),   //# "电子凭证不在有效期内"


    // 退货相关

    EV_ILLEGAL_QTY_ON_REFUND_ALL("20810201", "|full.voucher.refund|, |return.quantity.all|");  //# "电子凭证全额退款, "退货件数必须全退

    // =================================

    String code;

    String script;

    EvoucherErrorCode(String code,  String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static EvoucherErrorCode codeOf(String code) {
        return Arrays.stream(EvoucherErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
