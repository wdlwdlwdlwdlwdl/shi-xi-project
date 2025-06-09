
package com.aliyun.gts.gmall.platform.trade.domain.entity.cart;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
public class CartGroup extends AbstractBusinessEntity {

    @ApiModelProperty("商品列表")
    private List<CartItem> cartItems;

    @ApiModelProperty("分组类型, CartGroupTypeEnum, 其他值业务扩展")
    private Integer groupType;

    @ApiModelProperty("卖家分组 de 卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家分组 de 卖家名称")
    private String sellerName;

    @ApiModelProperty("购物车id")
    private Long cartId;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("是否可勾选下单")
    private Boolean selectEnable = Boolean.TRUE;

    @ApiModelProperty("分组商品总价")
    private Long groupTotalPrice;

    @ApiModelProperty("分组商品营销价格")
    private Long groupPromotionPrice;

    @ApiModelProperty("分组商品数量")
    private Integer groupItemTotal;

    /**
     * 计算求和
     */
    public void calcTotal() {
        if (CollectionUtils.isNotEmpty(cartItems)) {
            this.setGroupTotalPrice(cartItems.stream().map(CartItem::getCartTotalPrice).reduce(Long::sum).get());
            this.setGroupPromotionPrice(cartItems.stream().map(CartItem::getCartPromotionPrice).reduce(Long::sum).get());
            this.setGroupItemTotal(cartItems.stream().map(CartItem::getQuantity).reduce(Integer::sum).get());
        }
    }
}
