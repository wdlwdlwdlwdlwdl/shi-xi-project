package com.aliyun.gts.gmall.manager.front.trade.dto.output.cart;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品按照买家分组结果
 *
 * @author tiansong
 */
@ApiModel("商品按照买家分组结果")
@Data
public class CartGroupVO extends AbstractOutputInfo {
    @ApiModelProperty("商品列表,默认空List")
    private List<CartItemVO> items;

    @ApiModelProperty("卖家分组-卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家分组-卖家名称")
    private String sellerName;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("购物车ID")
    private Long cartId;

    @ApiModelProperty("可选中")
    private Boolean selectEnable = true;

    @ApiModelProperty("分组商品总价")
    private Long groupTotalPrice;

    @ApiModelProperty("分组商品营销价格")
    private Long groupPromotionPrice;

    @ApiModelProperty("分组商品数量")
    private Integer groupItemTotal;
}
