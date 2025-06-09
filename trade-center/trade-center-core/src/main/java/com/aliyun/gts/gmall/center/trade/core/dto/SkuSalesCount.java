package com.aliyun.gts.gmall.center.trade.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuSalesCount {
    /**
     * 卖家ID
     */
    private Long sellerId;
    /**
     * 商品ID
     */
    private Long itemId;
    /**
     * skuID
     */
    private Long skuId;

    /**
     * 总销量
     */
    private Double skuTotalSales;
    /**
     * 近一个月销量
     */
    private Double skuLastThirtyDaySales;


    /**
     * 总销量
     */
    private Double itemTotalSales;
    /**
     * 近一个月销量
     */
    private Double itemLastThirtyDaySales;


    /**
     * 最后一次销售时间
     */
    private Long skuLastSaleDate;


    private Long itemLastSaleDate;




}
