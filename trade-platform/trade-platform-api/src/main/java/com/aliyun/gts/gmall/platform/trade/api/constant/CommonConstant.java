package com.aliyun.gts.gmall.platform.trade.api.constant;

/**
 * 公用常量
 * @anthor shifeng
 * @version 1.0.1
 * 2025-2-25 11:34:04
 */
public interface CommonConstant {

    // 21岁
    public static final Integer AGE = 1;

    public static final Integer DEFAULT_INSTALL = 3;

    // 21岁
    public static final Integer DEFAULT_RATE = 0;

    public static final String OPENSEARCH = "opensearch";

    public static final String ORDER_LOCK_KEY = "ORDER_LOCK_KEY_%s";

    public static final Integer ORDER_TIME_OUT = 3000;

    public static final Integer ORDER_MAX_TIME_OUT = 5000;

    public static final String REFUND_CONSUMER_LOCK_KEY = "REFUND_CONSUMER_LOCK_KEY_%s";

    public static final String ORDER_CANCEL_CONSUMER_LOCK_KEY = "ORDER_CANCEL_CONSUMER_LOCK_KEY_%s";

    public static final String REFUND_ORDER_LOCK_KEY = "REFUND_ORDER_LOCK_KEY_%s";

    public static final String USER_PICK_LOGISTICS = "USER_PICK_LOGISTICS_%s_%s";

    // 卖家运费分类
    public static final String ALL_CATEGORY_MERCHANT = "1";
    public static final String NOT_ALL_CATEGORY_MERCHANT = "0";

    public static final Integer MERCHANT_FEE_ACTIVE = 1;

    public static final Integer FEE_ACTIVE = 1;

    public static final String ORDER_AUTO_CANCEL = "ORDER_AUTO_CANCEL_%s";

    public static final String CANCEL_CODE = "CANCEL_CODE_%s";

}

