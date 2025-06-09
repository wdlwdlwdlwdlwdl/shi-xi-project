package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import lombok.Data;

@Data
public class SkuQuoteCityPriceTemp {

    /**
     * 城市code
     */
    private String cityCode;
    /**
     * 最小分期数
     */
    private Long minPrice;
    /**
     * 最小分期数
     */
    private Integer minType;

    /** 划线价  */
    private Long originPrice;

    /**
     * 自低价对应最大分期数价格
     */
    private Long maxPrice;

    /**
     * 最大分期数
     */
    private Integer maxType;
}
