package com.aliyun.gts.gmall.manager.front.item.dto.temp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
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
public class ItemSaleSellerTempVO {
    // ---------------以下ES数据---------------
    private Long skuQuoteId;
    private Long itemId;
    private Long skuId;
    private Long sellerId;
    private Long categoryId;

    /** top1,top2标记 **/
    private String topFlag;
    /** 商品sku编码 */
    private String skuCode;
    /** 商家自定义sku编码 */
    private String sellerSkuCode;
    /** 分期价格 */
    private Long loanPeriodPrice;
    /** 分期期数 */
    private Integer loanPeriodType;
    /** 价格 */
    private List<LoanPeriodDTO> priceList;

    // ---------------以下是需要rpc服务的数据---------------
    // ----------商家信息相关
    /** op商家 */
    private Boolean op;
    /** ka商家 */
    private Boolean ka;
    /** 店铺名称 */
    private String shopName;
    /** 商家评分平均分 */
    private BigDecimal avgScore = BigDecimal.ZERO;
    /** 商家评论数 */
    private Long commentCoun = 0L;
    /** 商家订单数 */
    private Long orderCount = 0L;

    /**
     * 是否是可靠商家
     */
    private Boolean hasReliableSeller;
    /** 关联的品牌 */
    private List<Long> brandIds;

    // ----------库存相关
    /** 库存信息 */
    private List<SkuQuoteWarehourseStockDTO> warehourseStocks;
    /** 是否有库存 */
    private Boolean haveInventory = false;

    // ----------价格相关
    /** 折扣价 */
    private Long promPrice = 0L;
    /** 划线价(原价) */
    private Long originPrice;
    /** 总销量 */
    private Long totalSaleCount = 0L;
    /** 预售定金价格 */
    private Long presalePrice = 0L;
    /** 预售定尾款价 */
    private Long presaleFinalPrice = 0L;
    /** 活动类型code(预售,秒杀) */
    private String promToolCode;

    // ----------物流相关
    /** 3小时送达 */
    private Boolean hasThridHoursToReach = false;
    /** 银行网点服务 */
    private Boolean hasPvzPickup = false;
    /** 自提柜服务 */
    private Boolean hasPostamatPickup = false;
    /** 卖家自有物流,送货上门 */
    private Boolean hasHomeDeliveryService = false;
    /** 卖家仓库提货 */
    private Boolean hasWarehousePickup = false;

    /** 是否是自有物流: 1:hm物流 2:自有物流 -1:无 */
    private Integer deliveryType;
    /** 物流送达时间 */
    private String deliverDate;
    /** 物流时效 */
    private Integer deliveryTimeliness;
    /** 免费送货阈值 -1:免费, */
    private long freeDeliveryThreshold;
    /** 物流费用 */
    private long shippingFee;

    /** 是否物流覆盖 */
    private Boolean hasDeliveryCover;

    // 重新计算优惠价格
    private boolean reducePromotion;

    /**
     * 拇指商家逻辑
     *
     * @return boolean
     */
    public Boolean getIstHumbShop() {
        if (Objects.isNull(avgScore) || Objects.isNull(orderCount)) {
            return false;
        }
        return avgScore.compareTo(new BigDecimal(4)) > 0 && orderCount > 100;
    }

    /**
     * 优先使用折扣价计算分期价
     */
    public Long getLoanPeriodPrice() {
        if (loanPeriodType != null && promPrice != null && promPrice > 0) {
            return Math.round((double) promPrice / loanPeriodType);
        }
        if (loanPeriodType == null || loanPeriodPrice == null) {
            return 0L;
        }
        return Math.round((double) loanPeriodPrice / loanPeriodType);
    }

    /**
     *
     * @return
     */
    public boolean isDeliveryFeeFree() {
        if (Boolean.TRUE == hasHomeDeliveryService && promPrice.longValue() > 0 && freeDeliveryThreshold > 0 && shippingFee > 0) {
            return promPrice >= freeDeliveryThreshold;
        }
        return false;
    }
}
