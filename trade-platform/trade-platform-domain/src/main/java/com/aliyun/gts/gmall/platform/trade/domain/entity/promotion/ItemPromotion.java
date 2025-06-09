package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemPromotion extends AbstractBusinessEntity {

    @ApiModelProperty("商品SKU ID")
    private ItemSkuId itemSkuId;

    @ApiModelProperty("商品数量")
    private Integer skuQty;

    @ApiModelProperty("营销一口价(单价)")
    private Long itemPrice;

    @ApiModelProperty("一口价优惠名称")
    private String itemPriceName;

    @ApiModelProperty("营销返回的最终分摊价格(非单价)")
    private Long promotionPrice;

    @ApiModelProperty("IC原价(单价)")
    private Long originPrice;

    @ApiModelProperty("支付方式价格)")
    private List<PayModeItemPrice> payModePrices;

    @ApiModelProperty("营销返回的活动分摊明细")
    private List<ItemDivideDetail> itemDivideDetails;

    @ApiModelProperty("商品信息,仅作为营销查询时的入参")
    private ItemSku itemSku;
}
