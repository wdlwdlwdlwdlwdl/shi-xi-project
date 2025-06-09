package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单上用户展示价格的集合
 * orderTotalAmt = orderDiscountAmt + freightAmt
 * orderTotalAmt = orderRealAmt + pointAmt
 * orderSaleAmt = singleItemPrice * 商品数量
 */
@Data
public class OrderDisplayPriceDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "原价汇总金额")
    private Long itemOriginAmt;

    @ApiModelProperty(value = "营销一口价汇总金额")
    private Long itemAmt;

    @ApiModelProperty(value = "营销最终优惠后价格")
    private Long orderPromotionAmt;

    @ApiModelProperty(value = "折扣金额")
    private Long reduce;

    @ApiModelProperty(value = "实付现金 的订单分摊金额, 含运费, 扣除积分抵扣")
    private Long orderRealAmt;

    @ApiModelProperty(value = "运费金额")
    private Long freightAmt;

    @ApiModelProperty(value = "折扣金额")
    private Long itemDisCountAmt;

    @ApiModelProperty(value = "积分抵扣金额(金本位)")
    private Long pointAmt;

    @ApiModelProperty(value = "单品原价(单价)")
    private Long originPrice;

    @ApiModelProperty(value = "营销一口价(单价)")
    private Long itemPrice;

    @ApiModelProperty(value = "积分数量(原子积分)")
    private Long pointCount;

    @ApiModelProperty(value = "金本位总费用 的订单分摊金额, orderRealAmt + pointAmt")
    public Long getOrderTotalAmt() {
        return NumUtils.getNullZero(orderRealAmt) + NumUtils.getNullZero(pointAmt);
    }

    @ApiModelProperty(value = "确认收货金额, 用于打款、分账等")
    private ConfirmPriceDTO confirmPrice;


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
