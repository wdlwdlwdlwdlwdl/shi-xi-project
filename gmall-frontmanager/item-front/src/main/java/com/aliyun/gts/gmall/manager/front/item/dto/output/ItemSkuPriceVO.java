package com.aliyun.gts.gmall.manager.front.item.dto.output;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Data
public class ItemSkuPriceVO {
    /**  现价 */
    private Long promPrice;
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

    /**
     * 商家id
     */
    private Long sellerId;
    /**
     * 商家skuCode
     */
    private String sellerSkuCode;

    /** 商家skuCode */
    private String skuCode;
    /** 预售定金价格 */
    private Long presalePrice = 0L;
    /** 预售定尾款价 */
    private Long presaleFinalPrice = 0L;
    /** 活动类型code(预售，秒杀) **/
    private String promToolCode;

    public String getDiscountRate() {
        if (promPrice == null || promPrice == 0 || originPrice == null
                || originPrice.equals(promPrice)) {
            return null;
        }
        if (originPrice < promPrice) {
            return null;
        }
        BigDecimal divide = BigDecimal.valueOf(originPrice - promPrice).divide(BigDecimal.valueOf(originPrice), 4, RoundingMode.DOWN);
        DecimalFormat decimalFormat = new DecimalFormat("0%");
        return decimalFormat.format(divide.doubleValue());
    }
   
}
