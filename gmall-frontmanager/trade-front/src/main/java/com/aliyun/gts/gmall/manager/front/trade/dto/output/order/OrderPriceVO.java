package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderPriceVO {

    @ApiModelProperty(value = "原价汇总金额")
    private Long itemOriginAmt;

    @ApiModelProperty(value = "营销一口价汇总金额")
    private Long itemAmt;

    @ApiModelProperty(value = "营销最终优惠后价格")
    private Long orderPromotionAmt;

    @ApiModelProperty(value = "实付现金 的订单分摊金额, 含运费, 扣除积分抵扣")
    private Long orderRealAmt;

    @ApiModelProperty(value = "运费金额")
    private Long freightAmt;

    @ApiModelProperty(value = "积分抵扣金额(金本位)")
    private Long pointAmt;

    @ApiModelProperty(value = "单品原价(单价)")
    private Long originPrice;

    @ApiModelProperty(value = "营销一口价(单价)")
    private Long itemPrice;

    @ApiModelProperty(value = "商品总价")
    private Long itemTotalAmt;

    @ApiModelProperty(value = "主订单上的实付款")
    private Long totalAmt;


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
