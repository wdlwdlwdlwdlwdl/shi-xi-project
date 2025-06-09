package com.aliyun.gts.gmall.manager.front.item.dto.output;

import lombok.Data;

@Data
public class ItemSkuCartPriceVO {

    private Boolean addCart;

    /**
     * 原价
     */
    private Long itemPrice;

    /**
     * 营销价格
     */
    private Long itemPromotionPrice;


    /**
     * 营销价格 - 单价
     */
    private Long itemUnitPromotionPrice;

    /**
     * 营销工具
     */
    private String promotionTool;

    /**
     * 营销首付
     */
    private Long firstPromotionPrice;

    /**
     * 营销尾款
     */
    private Long balancePromotionPrice;

}
