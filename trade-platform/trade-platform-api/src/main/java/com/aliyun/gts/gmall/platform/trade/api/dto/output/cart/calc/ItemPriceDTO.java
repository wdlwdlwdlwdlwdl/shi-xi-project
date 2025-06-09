package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.ItemDividePromotionDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemPriceDTO extends AbstractOutputInfo {

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("sku ID")
    private Long skuId;

    @ApiModelProperty("商品数量（购物车中）")
    private Long skuQty;

    @ApiModelProperty("营销一口价（单价）")
    private Long itemPrice;

    @ApiModelProperty("营销一口价活动名称")
    private String itemPriceName;

    @ApiModelProperty("营销返回的最终价格")
    private Long promotionPrice;

    @ApiModelProperty("营销返回的活动分摊明细")
    private List<ItemDividePromotionDTO> itemDivideDetails;
}
