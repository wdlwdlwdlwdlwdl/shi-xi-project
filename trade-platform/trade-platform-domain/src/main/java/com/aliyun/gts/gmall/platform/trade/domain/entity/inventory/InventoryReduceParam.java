package com.aliyun.gts.gmall.platform.trade.domain.entity.inventory;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryReduceParam {

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("SKU ID")
    private ItemSkuId skuId;

    @ApiModelProperty("sku数量")
    private Integer skuQty;
}
