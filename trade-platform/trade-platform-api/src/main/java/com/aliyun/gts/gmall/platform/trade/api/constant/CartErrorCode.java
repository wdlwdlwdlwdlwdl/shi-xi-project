package com.aliyun.gts.gmall.platform.trade.api.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

public enum CartErrorCode implements ResponseCode {

    CART_ITEM_INVENTORY_NOT_ENOUGH("20120001", "|cart.stock.insufficient|"),   //# "商品加购库存不足"
    CART_ITEM_NOT_EXISTS("20120002", "|cart.item.not.exists|"),
    CART_USER_NOT_EXISTS("20120012", "|user.item.not.exists|"),
    CART_ITEM_STATUS_INVALID("20120032", "|cart.item.status.invalid|"),
    CART_SELL_STATUS_INVALID("20120033", "|cart.sell.status.invalid|"),
    CART_QTY_OUT_LIMIT("20120003", "|cart.full|,|qty.limit.exceed|"),  //# "购物车已满, "超出数量限制
    CART_NOT_SUPPORT_PRESALE("20120007", "|cart.presale.not.support|"),
    CART_NOT_SUPPORT_AWARD("20120009", "|cart.award.not.support|"),
    CART_NOT_SUPPORT_SECKILL("20120011", "|cart.seckill.not.support|");

    // =================================

    String code;

    String script;

    CartErrorCode(String code,  String script) {
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
    public String getScript() {
        return script;
    }
}
