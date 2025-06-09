package com.aliyun.gts.gmall.center.trade.common.constants;

/**
 * 对公支付等 确认付款 收款等信息在orderpay中的feature用到的key
 */
public class CATPayFeatureKeys {

    /**
     * 买家确认打款
     */
    public static final String BuyerConfirmTransfered = "transfered";
    /**
     * 卖家确认收款
     */
    public static final String SellerConfirmReceived = "received";

    /**
     * 卖家确认收款时间
     */
    public static final String SellerConfirmTime = "sellerConfirmTime";

    /**
     * 卖家确认收款时的备注
     */
    public static final String Memo = "catMemo";
    /**
     * 打款的流水id
     */
    public static final String CATFlowId = "catFlowId";
    /**
     * 卖家关闭支付单的原因
     */
    public static final String ColseType = "closeType";

    /**
     * 是否关闭, Boolean
     */
    public static final String Closed = "closed";

    /**
     * 实际打款时间
     */
    public static final String RealPayTime = "realPayTime";

    public static final String TAG_SELLER_RECEIVED = "sellerReceived";
    public static final String TAG_SELLER_CLOSED = "sellerClosed";

    public static final String VOUCHERS = "vouchers";
}
