package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import org.apache.commons.collections4.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: SearchThirdItemVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月28日 17:42:20
 * @version V1.0
 */
@Getter
@Setter
public class SearchThirdItemVO {
    private Long itemId;
    private Long skuId;

    private Long categoryId;
    private Long brandId;
    private String title;
    /** sku图片 */
    private List<String> images;
    /** sku评分 */
    private String score;
    /** sku评论数 */
    private Long commentTotal;
    /** 订单数量 */
    private Long orderTotal;
    /** 分期数 */
    private Integer loadType;
    /** 最低价格 */
    private Long priceLowest;
    /** 营销价格 */
    private Long pricePromotion;
    /** 高亮的标题 */
    private String titleHighlight;

    /** 获取主图 */
    public String getMainImage() {
        if (CollectionUtils.isEmpty(images)) {
            return null;
        }

        return images.get(0);
    }

    /** 获取优惠比例 */
    public String getDiscountRatio() {
        if (Objects.isNull(pricePromotion) || Objects.isNull(pricePromotion) || 0 == priceLowest || 0 == pricePromotion) {
            return "N/A";
        }

        BigDecimal lowestPrice = BigDecimal.valueOf(priceLowest);
        BigDecimal promotionPrice = BigDecimal.valueOf(pricePromotion);

        // 计算差值
        BigDecimal difference = lowestPrice.subtract(promotionPrice);

        // 计算百分比
        BigDecimal percentage = difference.divide(lowestPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        // 四舍五入并取整
        int roundedPercentage = percentage.setScale(0, RoundingMode.HALF_UP).intValue();

        // 格式化输出
        return roundedPercentage + "%";
    }

}
