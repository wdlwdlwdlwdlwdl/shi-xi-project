package com.aliyun.gts.gmall.manager.front.trade.constants;

/**
 * @Description
 * @Author FaberWong
 * @Date 2024/9/19 14:50
 */
public interface ItemUnableReason {


    /**
     * item下架
     */
    String ITEM_UNAVAILABLE = "ITEM_UNAVAILABLE";

    /**
     * sku下架
     */
    String SKU_UNAVAILABLE = "SKU_UNAVAILABLE";

    /**
     * 库存不足
     */
    String SKU_QUANTITY_NOT_ENOUGH = "SKU_QUANTITY_NOT_ENOUGH";

    /**
     * 商家不可用
     */
    String SELLER_UNAVAILABLE = "SELLER_UNAVAILABLE";

    /**
     * loan期数不可用了
     */
    String LOAN_NUMBER_UNAVAILABLE = "LOAN_NUMBER_UNAVAILABLE";

    /**
     * INSTALLMENT期数不可用了
     */
    String INSTALLMENT_NUMBER_UNAVAILABLE = "INSTALLMENT_NUMBER_UNAVAILABLE";
}
