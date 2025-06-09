package com.aliyun.gts.gmall.manager.front.common.consts;

/**
 * 数据源唯一ID定义
 *
 * @author tiansong
 */
public class DsIdConst {

    /**
     * 城市查询
     */
    public static final String city_all            = "city_all";

    /**
     * 会员-登录操作
     */
    public static final String login_security_sendCode      = "login_security_sendCode";
    public static final String login_security_checkCode     = "login_security_checkCode";
    public static final String login_security_checkPwd      = "login_security_checkPwd";
    public static final String login_customer_queryByPhone  = "login_customer_queryByPhone";
    public static final String login_customer_create        = "login_customer_create";
    public static final String login_customer_bindPhone     = "login_customer_bindPhone";
    public static final String login_customer_queryByOpenId = "login_customer_queryByOpenId";
    public static final String login_customer_bindWeiXin    = "login_customer_bindWeiXin";

    /**
     * 会员通用操作
     */
    public static final String user_security_code_send           = "user_security_code_send";
    public static final String user_security_code_check          = "user_security_code_check";
    public static final String user_customer_create              = "user_customer_create";
    public static final String user_customer_extend_save         = "user_customer_extend_save";
    public static final String user_customer_status_update       = "user_customer_status_update";
    public static final String user_customer_extend_query        = "user_customer_extend_query";


    /**
     * 会员-基础信息操作
     */
    public static final String customer_base_queryById         = "customer_base_queryById";
    public static final String customer_base_queryByCustInfo   = "customer_base_queryByCustInfo";
    public static final String customer_base_checkPwd          = "customer_base_checkPwd";
    public static final String customer_base_update            = "customer_base_update";
    public static final String customer_base_queryLevel        = "customer_base_queryLevel";
    public static final String customer_base_queryLevelConfig  = "customer_base_queryLevelConfig";
    /**
     * 会员-收货地址
     */
    public static final String customer_address_createOrUpdate = "customer_address_createOrUpdate";
    public static final String customer_address_queryById      = "customer_address_queryById";
    public static final String customer_address_queryList      = "customer_address_queryList";
    public static final String customer_address_delById        = "customer_address_delById";
    public static final String customer_address_setDefault     = "customer_address_setDefault";
    /**
     * 会员-发票
     */
    public static final String customer_invoice_queryList      = "customer_invoice_queryList";
    public static final String customer_invoice_create         = "customer_invoice_create";
    public static final String customer_invoice_update         = "customer_invoice_update";
    public static final String customer_invoice_delete         = "customer_invoice_delete";
    /**
     * 会员-其他
     */
    public static final String customer_coupon_batchQuery      = "customer_coupon_batchQuery";
    public static final String customer_coupon_pageQuery       = "customer_coupon_pageQuery";
    public static final String customer_new_predict = "customer_new_predict";
    public static final String customer_group_relation = "customer_group_relation";
    public static final String customer_seller_relation = "customer_seller_relation";
    public static final String customer_seller_relation_save = "customer_seller_relation_save";
    public static final String customer_seller_query = "customer_seller_query";

    public static final String customer_growth_query = "customer_growth_query";
    public static final String customer_growth_type_query = "customer_growth_type_query";
    public static final String customer_seller_indicator_query = "customer_seller_indicator_query";
    public static final String customer_seller_base_query = "customer_seller_base_query";
    public static final String customer_seller_shop_query = "customer_seller_shop_query";
    public static final String customer_extend_query = "customer_extend_query";
    public static final String customer_seller_score_query = "customer_seller_score_query";
    /**
     * 商品详情
     */
    public static final String item_detail_query               = "item_detail_query";
    public static final String item_promotion_query            = "item_promotion_query";
    public static final String item_customer_queryBatch        = "item_customer_queryBatch";
    public static final String item_search_queryItemBatch      = "item_search_queryItemBatch";
    public static final String item_search_queryEvaluation     = "item_search_queryEvaluation";
    public static final String item_search_queryReply          = "item_search_queryReply";
    public static final String item_address_query              = "item_address_query";
    public static final String item_address_queryDefault       = "item_address_queryDefault";
    public static final String item_freight_query              = "item_freight_query";
    public static final String item_agreement_query            = "item_agreement_query";
    public static final String item_sku_query                  = "item_sku_query";
    public static final String item_sku_features_query         = "item_sku_features_query";
    public static final String item_detail_v2_query             = "item_detail_v2_query";
    public static final String item_deadline_query             = "item_deadline_query";
    public static final String item_characteristics_query      = "item_characteristics_query";
    public static final String item_page_query                  = "item_page_query";
    public static final String item_sku_quote_warehouse_query                  = "item_sku_quote_warehouse_query";

    public static final String category_all_query                  = "category_all_query";

    public static final String category_prop_query              = "category_prop_query";
    
    public static final String category_prop_value_query = "category_prop_value_query";
    
    /**
     * 交易-订单请求
     */
    public static final String trade_order_confirm        = "trade_order_confirm";
    public static final String trade_order_checkout       = "trade_order_checkout";
    public static final String trade_order_create         = "trade_order_create";
    public static final String trade_order_cancel         = "trade_order_cancel";
    public static final String trade_order_del            = "trade_order_del";
    public static final String trade_order_receipt        = "trade_order_receipt";
    public static final String trade_order_confirmStepOrder = "trade_order_confirmStepOrder";
    public static final String trade_order_getDetail      = "trade_order_getDetail";
    public static final String trade_order_getList        = "trade_order_getList";
    public static final String trade_order_queryLogistics = "trade_order_queryLogistics";
    public static final String trade_order_checkSplit =     "trade_order_checkSplit";
    public static final String trade_user_pick        = "trade_user_pick";

    /**
     * 交易-购物车请求
     */
    public static final String trade_cart_quantity  = "trade_cart_quantity";
    public static final String trade_cart_queryList = "trade_cart_queryList";
    public static final String trade_cart_add       = "trade_cart_add";
    public static final String trade_cart_modify    = "trade_cart_modify";
    public static final String trade_cart_singleQuery = "trade_cart_singleQuery";
    public static final String trade_cart_delete    = "trade_cart_delete";
    public static final String trade_cart_checkAdd  = "trade_cart_checkAdd";
    public static final String trade_cart_queryQty  = "trade_cart_queryQty";
    public static final String trade_cart_query_payMode  = "trade_cart_query_payMode";
    public static final String trade_cart_calPrice  = "trade_cart_calPrice";

    /**
     * 交易-支付请求
     */
    public static final String trade_pay_render           = "trade_pay_render";
    public static final String trade_pay_check            = "trade_pay_check";
    public static final String trade_pay_toPay            = "trade_pay_toPay";
    public static final String trade_pay_toMergePay       = "trade_pay_toMergePay";
    public static final String trade_pay_callback         = "trade_pay_callback";
    /**
     * 交易-售后请求
     */
    public static final String trade_reversal_queryList   = "trade_reversal_queryList";
    public static final String trade_reversal_reasonList  = "trade_reversal_reasonList";
    public static final String trade_reversal_queryDetail = "trade_reversal_queryDetail";
    public static final String trade_reversal_checkOrder  = "trade_reversal_checkOrder";

    public static final String trade_reversal_create            = "trade_reversal_create";
    public static final String trade_reversal_sendDeliver       = "trade_reversal_sendDeliver";
    public static final String trade_reversal_cancel            = "trade_reversal_cancel";

    public static final String trade_buyer_confrim_refund            = "trade_buyer_confrim_refund";
    /**
     * 交易-其他请求
     */
    public static final String trade_item_queryBatch            = "trade_item_queryBatch";
    public static final String trade_sku_queryBatch             = "trade_sku_queryBatch";
    public static final String trade_customer_query             = "trade_customer_query";
    public static final String trade_customer_address_queryById = "trade_customer_address_queryById";
    public static final String trade_customer_address_default   = "trade_customer_address_default";
    public static final String trade_count_query                = "trade_count_query";
    public static final String trade_evaluation_add             = "trade_evaluation_add";
    public static final String trade_seller_queryBatch          = "trade_seller_queryBatch";
    public static final String trade_seller_queryShop           = "trade_seller_queryShop";

    /**
     * 营销-分享裂变请求
     */
    public static final String promo_share_campaign_query = "promo_share_campaign_query";
    public static final String promo_share_campaign_launch = "promo_share_campaign_launch";
    public static final String promo_share_campaign_vote = "promo_share_campaign_vote";
    public static final String promo_share_campaign_obtain = "promo_share_campaign_obtain";

    /**
     * misc
     */
    public static final String misc_dict_query = "misc_dict_query";


    /**
     * 发票
     */
    public static final String trade_invoice_apply       = "trade_invoice_apply";
    public static final String trade_invoice_return      = "trade_invoice_return";
    public static final String trade_invoice_reject       = "trade_invoice_reject";

    /**
     * api-pay 接口支付
     */
    public static final String api_pay = "api_pay";

    public static final String api_card_query = "api_card_query";

    public static final String api_pay_refund = "api_pay_refund";

    public static final String api_card_add = "api_card_add";
}
