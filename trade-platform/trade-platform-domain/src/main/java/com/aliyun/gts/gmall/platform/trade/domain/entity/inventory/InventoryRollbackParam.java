package com.aliyun.gts.gmall.platform.trade.domain.entity.inventory;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryRollbackParam {

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("逆向子单ID")
    private Long reversalId;

    @ApiModelProperty("SKU ID")
    private ItemSkuId skuId;

    @ApiModelProperty("sku数量")
    private Integer skuQty;
}
