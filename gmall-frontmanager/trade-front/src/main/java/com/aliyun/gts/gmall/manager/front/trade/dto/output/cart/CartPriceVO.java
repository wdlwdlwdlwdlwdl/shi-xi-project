package com.aliyun.gts.gmall.manager.front.trade.dto.output.cart;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionOptionVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionPriceReductionVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.SellerPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.SellerSkuPriceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 购物车勾选计算下单价格
 *
 * @author tiansong
 */
@ApiModel("购物车勾选计算下单价格")
@Data
public class CartPriceVO extends AbstractOutputInfo {
    @ApiModelProperty("营销返回的最终价格")
    private Long promotionPrice;
    @ApiModelProperty("优惠选项")
    private List<PromotionOptionVO> options;
    @ApiModelProperty("店铺明细")
    private List<SellerPriceVO> sellers;
    @ApiModelProperty("跨店优惠")
    private List<PromotionPriceReductionVO> crossSellerReducePrices;
    @ApiModelProperty("店铺内优惠")
    private List<PromotionPriceReductionVO> sellerReducePrices;
    @ApiModelProperty("总的价格优惠")
    private Long totalReducePrice;
    @ApiModelProperty("首笔支付金额")
    private Long firstPayPrice;
    /**
     * 超过支付限制
     */
    private Boolean overPayLimit;
    /**
     * 支付上限
     */
    private Long payLimit;

    @ApiModelProperty("店铺明细")
    private List<SellerSkuPriceVO> sellerSkus;

    public String getPromotionPriceYuan() {
        return String.valueOf(this.getPromotionPrice());
    }

    public String getTotalReducePriceYuan() {
        return String.valueOf(this.getTotalReducePrice());
    }

    public String getFirstPayPriceYuan() {
        return firstPayPrice == null ? null : String.valueOf(firstPayPrice);
    }

    public String getSellerTotalReducePriceYuan() {
        // 店铺优惠
        if (CollectionUtils.isEmpty(sellerReducePrices)) {
            return "0";
        }
        return String.valueOf(getSellerTotalReducePrice());
    }

    public String getPayLimitYuan() {
        if (payLimit == null) {
            return "0";
        }
        return String.valueOf(payLimit);
    }

    public Long getSellerTotalReducePrice() {
        // 店铺优惠
        if (CollectionUtils.isEmpty(sellerReducePrices)) {
            return 0L;
        }
        return sellerReducePrices.stream().map(PromotionPriceReductionVO::getReduceFee).reduce((o, o2) -> o + o2).get();
    }

    public String getCrossSellerReducePriceYuan() {
        // 平台优惠
        if (CollectionUtils.isEmpty(crossSellerReducePrices)) {
            return "0";
        }
        return String.valueOf(getCrossSellerReducePrice());
    }

    public Long getCrossSellerReducePrice() {
        // 平台优惠
        if (CollectionUtils.isEmpty(crossSellerReducePrices)) {
            return 0L;
        }
        return crossSellerReducePrices.stream().map(PromotionPriceReductionVO::getReduceFee)
            .reduce((o, o2) -> o + o2).get();
    }

    public String getSaleAmtYuan() {
        // 商品总价; itemPrice * skuQty 加和
        return String.valueOf(getSaleAmt());
    }

    public Long getSaleAmt() {
        // 商品总价; itemPrice * skuQty 加和
        return sellers.stream().flatMap(sellerPriceVO -> sellerPriceVO.getItems().stream())
            .mapToLong(itemPrice -> itemPrice.getItemPrice() * itemPrice.getSkuQty()).sum();
    }

    public String getSaleAmtWithReduceYuan() {
        return String.valueOf(getSaleAmtWithReduce());
    }

    public Long getSaleAmtWithReduce() {
        //总售价-总优惠
        return getSaleAmt() - getTotalReducePrice();
    }
}
