package com.aliyun.gts.gmall.platform.trade.domain.entity.cart;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class Cart extends AbstractBusinessEntity {

    @ApiModelProperty("购物车商品分组")
    private List<CartGroup> groups;

    @ApiModelProperty("购物车商品总量")
    private Integer totalItemCount;

    @ApiModelProperty("购物车类型")
    private Integer cartType;

    @ApiModelProperty("购物车优惠明细")
    private OrderPromotion promotions;

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("购物车根据卖家商品分组")
    private List<CartGroup> groupsBySeller;

}
