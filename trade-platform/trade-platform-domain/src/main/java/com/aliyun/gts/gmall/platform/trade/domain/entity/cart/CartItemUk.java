package com.aliyun.gts.gmall.platform.trade.domain.entity.cart;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车查询的业务唯一键
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUk {

    private Long custId;

    private Integer cartType;

    private ItemSkuId itemSkuId;

    private Long sellerId;

    private String payMode;

    public CartItemUk(Long custId, Integer cartType, ItemSkuId itemSkuId) {
        this.custId = custId;
        this.cartType = cartType;
        this.itemSkuId = itemSkuId;
    }

}
