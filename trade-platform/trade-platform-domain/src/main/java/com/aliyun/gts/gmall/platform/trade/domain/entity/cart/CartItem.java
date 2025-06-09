
package com.aliyun.gts.gmall.platform.trade.domain.entity.cart;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartItem extends TcCartDO {

    @ApiModelProperty("商品信息")
    private ItemSku itemSku;

    @ApiModelProperty("是否已经查询不到该商品或SKU（删除等）")
    private boolean itemNotFound;

    @ApiModelProperty("购物车ID")
    private Long cartId;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("价格状态")
    private Boolean priceChangeStatus;

    @ApiModelProperty("SKU状态")
    private Boolean skuChangeStatus;

    @ApiModelProperty("卖家状态")
    private Boolean sellerChangeStatus;

    @ApiModelProperty("商品状态")
    private Boolean itemChangeStatus;

    @ApiModelProperty("分期变化")
    private Boolean installChangeStatus;

    @ApiModelProperty("购物车总价")
    private Long cartTotalPrice;

    @ApiModelProperty("购物车优惠价")
    private Long cartPromotionPrice;

    @ApiModelProperty("单品折扣价(单价-分)")
    private Long  itemUnitPrice;

    @ApiModelProperty("原始的installment分期数")
    private Integer originInstallment;

    @ApiModelProperty("当前的installment分期数")
    private Integer currentInstallment;

    @ApiModelProperty("登录用户的年龄")
    private Integer age = 0;

    @ApiModelProperty("是否可勾选下单")
    private Boolean selectEnable = Boolean.TRUE;

    @ApiModelProperty("营销返回的活动分摊明细")
    private List<ItemDivideDetail> itemDivideDetails;

    @ApiModelProperty("分组前支付方式 epay loan_期数 installment_期数")
    private String originalPayMode;

}

