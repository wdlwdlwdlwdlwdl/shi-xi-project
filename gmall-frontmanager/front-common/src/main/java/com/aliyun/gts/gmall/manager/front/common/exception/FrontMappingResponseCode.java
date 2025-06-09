package com.aliyun.gts.gmall.manager.front.common.exception;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 前台应用统一的错误码
 *
 * @author tiansong
 */
public enum FrontMappingResponseCode implements ResponseCode {
    ITEM_TRADE_FAILED("60090001", "|product.not.orderable|，|purchase.other.product|！",  0),  //# "该商品不可下单，请购买其他商品
    ITEM_EVOUCHER_EXPIRE("60090002", "|e.voucher.expired|！",  0),  //# "电子凭证已过期
    CART_QTY_OUT_LIMIT("60090003", "|cart.qty.limit|！",  0),  //# "购物车数量已达上限
    // 价格风控规则拦截
    TRADE_PRICE_RISK_ERROR("60090004", "|product.temporarily.unorderable|，|contact.seller|",  0),  //# "该商品暂时无法下单，请联系商家"
    ;

    private String code;
    private String script;
    private int    args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.script;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private FrontMappingResponseCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }
}
