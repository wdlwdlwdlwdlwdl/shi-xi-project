package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SellerSkuPriceVO {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("商品ID")
    private Long  itemId;

    @ApiModelProperty("sku ID")
    private Long  skuId;

    @ApiModelProperty("商品数量（购物车中")
    private Long  skuQty;
    @NotNull
    @ApiModelProperty("单品折扣价格")
    private Long  itemPrice;

    @ApiModelProperty("单品折扣名称")
    private String itemPriceName;

    @ApiModelProperty("营销返回的最终价格")
    private Long  promotionPrice;

    @ApiModelProperty("首笔支付金额 (非单价)")
    private Long firstPayPrice;
}
