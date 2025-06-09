package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 *  orderTotalAmt  (订单总价)
 *      = orderPromotionAmt + freightAmt   (优惠后商品金额 + 运费)
 *      = orderRealAmt + pointAmt          (实付 + 积分)
 */
@Data
public class OrderPrice extends ExtendComponent implements PayPrice {

    @ApiModelProperty(value = "原价汇总金额")
    private Long itemOriginAmt;

    @ApiModelProperty(value = "营销一口价汇总金额")
    private Long itemAmt;

    @ApiModelProperty(value = "营销最终优惠后价格")
    private Long orderPromotionAmt;

    @ApiModelProperty(value = "实付现金 的订单分摊金额, 含运费, 扣除积分抵扣")
    private Long orderRealAmt;

    @ApiModelProperty("下单商品实付金额, 不含运费")
    private Long orderRealItemAmt;

    @ApiModelProperty(value = "运费金额")
    private Long freightAmt;

    @ApiModelProperty(value = "积分抵扣金额")
    private Long pointAmt;

    @ApiModelProperty(value = "积分数量(原子积分)")
    private Long pointCount;

    @ApiModelProperty(value = "订单总的分摊金额, 即卖家实收金额, 等于 orderRealAmt + pointAmt, 也等于 orderPromotionAmt + freightAmt")
    public Long getOrderTotalAmt() {
        return NumUtils.getNullZero(orderRealAmt) + NumUtils.getNullZero(pointAmt);
    }

    @ApiModelProperty(value = "各类商品单价, 仅子订单有值")
    private ItemPrice itemPrice;

    @ApiModelProperty(value = "确认收货金额, 用于打款、分账等")
    private ConfirmPrice confirmPrice;

    @ApiModelProperty(value = "阶段分摊金额, 主订单为null, 子订单有值")
    private Map<Integer, StepOrderPrice> stepAmt;

    @ApiModelProperty(value = "折扣汇总金额")
    private Long itemDisCountAmt;

    @ApiModelProperty(value = "平台运费佣金")
    private Long deliveryMerchantFee;

    // 月金额
    private Long monthAmount;
    // 月金额 -- 营销后
    private Long monthPromotionAmount;
    // ============= 改价 =============

    @ApiModelProperty(value = "orderPromotionAmt 金额改动, 正改大、负改小")
    private Long adjustPromotionAmt;

    @ApiModelProperty(value = "freightAmt 金额改动, 正改大、负改小")
    private Long adjustFreightAmt;

    @ApiModelProperty(value = "pointAmt 金额改动, 正改大、负改小")
    private Long adjustPointAmt;

    @ApiModelProperty(value = "orderRealAmt 金额改动, 正改大、负改小")
    private Long adjustRealAmt;

    @ApiModelProperty(value = "pointCount 数量改动, 正改大、负改小")
    private Long adjustPointCount;

}
