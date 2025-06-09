package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSkuQty extends ItemSkuId {

    @ApiModelProperty("数量")
    private Integer qty;
}
