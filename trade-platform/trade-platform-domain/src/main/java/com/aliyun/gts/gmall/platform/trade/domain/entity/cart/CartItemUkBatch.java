package com.aliyun.gts.gmall.platform.trade.domain.entity.cart;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 购物车查询的业务唯一键（批量商品)
 */
@Data
public class CartItemUkBatch {

    private Long custId;

    private Integer cartType;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    private List<ItemSkuId> itemSkuIds;
    @ApiModelProperty("加入购物车选择的商家")

    private Long sellerId;
}
