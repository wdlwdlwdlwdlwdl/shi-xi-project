package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class ItemSaleSellerVO {

    // ---------------以下ES数据---------------
    private Long skuQuoteId;
    private Long itemId;
    private Long skuId;
    private Long sellerId;

    /** 分期价格 */
    private Long loanPeriodPrice;
    /** 分期期数 */
    @JsonIgnore
    private Integer loanPeriodType;
    /** 价格 */
    @JsonIgnore
    private List<LoanPeriodDTO> priceList;
    /** op商家 */
    private Boolean op;
    /** ka商家 */
    private Boolean ka;

    // ---------------以下是需要rpc服务的数据---------------
    /** 店铺名称 */
    private String shopName;
    /** 折扣价 */
    private Long promPrice;
    /** 拇指商家 */
    private Boolean istHumbShop;
    /** 总销量 */
    private Long totalSaleCount;
    /** 3小时送达 */
    private Boolean hasThridHoursToReach;
    /** 银行网点服务 */
    private Boolean hasPvzPickup;
    /** 自提柜服务 */
    private Boolean hasPostamatPickup;
    /** 卖家自有物流,送货上门 */
    private Boolean hasHomeDeliveryService;
    /** 卖家仓库提货 */
    private Boolean hasWarehousePickup;

    /** 查询指定的分期,如果没有,用自己的分期 */
    @JsonIgnore
    private Integer specificLoanPeriodType;

    /** 商家评分平均分*/
    private BigDecimal avgScore;
    /** 商家评论数 */
    private Integer commentCount;
    /** 商家订单数 */
    private Integer orderCount;

    /**
     * 分期计算
     *
     * @return
     */
    public LoanPeriodDTO getLoanPeriod() {
        return priceList.stream().filter(n -> specificLoanPeriodType == n.getType()).findFirst().orElse(new LoanPeriodDTO());
    }

    /**
     * 拇指商家逻辑
     * @return boolean
     */
    public Boolean getIstHumbShop() {
        if (avgScore == null || orderCount == null) {
            return false;
        }
        return avgScore.compareTo(new BigDecimal(4)) > 0 && orderCount > 100;
    }

    /**
     * 折扣计算
     *
     * @return
     */
    public BigDecimal getDiscount() {
        if(loanPeriodPrice == null || promPrice == null){
            return BigDecimal.ZERO;
        }
        if (loanPeriodPrice == promPrice) {
            return BigDecimal.ZERO;
        }

        BigDecimal a = BigDecimal.valueOf(promPrice);
        BigDecimal b = BigDecimal.valueOf(loanPeriodPrice);
        // 计算 (a - b) / a * 100
        return a.subtract(b).divide(a, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }
}
