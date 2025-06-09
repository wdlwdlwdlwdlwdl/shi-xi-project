package com.aliyun.gts.gmall.manager.front.item.dto.temp;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: ItemSaleSellerTempVO.java
 * @Description: 商品售卖商家列表-temp
 * @author zhao.qi
 * @date 2024年11月13日 12:21:39
 * @version V1.0
 */
@Getter
@Setter
public class ItemSaleSellerTempVO2 {
    private Long skuQuoteId;
    private Long itemId;
    private Long skuId;
    private Long sellerId;
    // 分期价格
    private Long loanPeriodPrice;
    // 分期期数
    private Integer loanPeriodType;
    // 总销量
    private Long totalSaleCount;
}
