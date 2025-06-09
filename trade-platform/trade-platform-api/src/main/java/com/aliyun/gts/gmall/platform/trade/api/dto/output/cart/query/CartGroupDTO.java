
package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CartGroupDTO extends AbstractOutputInfo {

    @ApiModelProperty("商品列表")
    private List<CartItemDTO> items;

    @ApiModelProperty("分组类型, 0=卖家分组, -1=废弃分组, 其他值业务扩展")
    private Integer groupType;

    @ApiModelProperty("卖家分组 de 卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家分组 de 卖家名称")
    private String sellerName;

    @ApiModelProperty("购物车ID")
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

}