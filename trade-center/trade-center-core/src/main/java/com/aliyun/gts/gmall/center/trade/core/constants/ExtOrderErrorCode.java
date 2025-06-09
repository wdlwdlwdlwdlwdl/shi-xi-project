package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ExtOrderErrorCode implements ResponseCode {

    ORDER_SKU_COUNT_OUT_LIMIT("20820001", "|exceed.item.limit|"),   //# "同时下单商品数超出限制"
    ORDER_QTY_LOW("20820002", "|below.min.quantity|"),   //# "不足商品起购数量"
    ORDER_QTY_HIGH("20820003", "|above.max.quantity.limit|"),   //# "超出商品限购数量"
    INVENTORY_NOT_ENOUGH("20820004", "|insufficient.combo.stock|"),   //# "组合商品库存不足"
    ORDER_ITEM_DISABLE_BUY("20820005", "|product.order.prohibited|"),   //# "商品禁止下单"
    REFUSE_ORDER_PRICE_RISK("20820006", "|incorrect.product.price|"),   //# "商品价格有误"
    FIXED_POINT_NOT_ENOUGH("20820007", "|insufficient.user.points|"),   //# "用户积分不足"

    B2B_SOURCING_BILL_NOT_ORD("20820008", "|bid.document.ordering.forbidden|"),   //# "决标单不能下单"
    B2B_SOURCING_BILL_ITEM_ILLEGAL("20820009", "|product.mismatch.bid.doc|"),   //# "下单商品与决标单不符"
    B2B_SOURCING_BILL_QTY_ILLEGAL("20820010", "|quantity.mismatch.bid.doc|"),   //# "下单数量与决标单不符"
    B2B_SOURCING_BILL_DUP_ORD("20820011", "|duplicate.order.on.bid.doc|"),   //# "决标单重复下单"
    B2B_SOURCING_PRICE_BEFORE_START("20820012", "|bidding.not.started|"),   //# "报价有效时间未开始"
    B2B_SOURCING_PRICE_AFTER_END("20820013", "|bidding.ended|"),   //# "报价有效时间结束"
    B2B_SOURCING_CONCURRENT_ORDER("20820014", "|concurrent.bidding.orders|,  |please.try.again.later|"), //# 决标单并发下单",  请稍后再试"
    ;


    // =================================

    private String code;

    private String script;

    ExtOrderErrorCode(String code, String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public int getArgs() {
        return 0;
    }

    public static ExtOrderErrorCode codeOf(String code) {
        return Arrays.stream(ExtOrderErrorCode.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
