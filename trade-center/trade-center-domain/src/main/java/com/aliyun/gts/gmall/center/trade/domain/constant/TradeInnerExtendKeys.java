package com.aliyun.gts.gmall.center.trade.domain.constant;

public class TradeInnerExtendKeys {

    // 商品

    // 是否协议价, boolean
    public static final String ITEM_IS_AGREEMENT = "ITEM_IS_AGREEMENT";

    /**
     * 是否代客下单价
     */
    public static final String ITEM_IS_HELP_ORDER_PRICE = "ITEM_IS_HELP_ORDER_PRICE";

    // 营销
    // 活动限购数量, int
    public static final String PROMOTION_BUY_LIMIT = "PROMOTION_BUY_LIMIT";
    // 活动下单单数限制, subOrder, BuyOrdsLimit
    public static final String PROMOTION_BUY_ORDS_LIMIT = "PROMOTION_BUY_ORDS_LIMIT";
    // 限购下单数已记录db, subOrder, boolean
    public static final String PROMOTION_IS_BUY_ORDS_INCR = "PROMOTION_IS_BUY_ORDS_INCR";

}
