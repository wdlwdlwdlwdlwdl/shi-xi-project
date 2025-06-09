package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.ItemDeliveryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单商品信息
 *
 * @author tiansong
 */
@Data
@ApiModel("订单商品信息")
public class OrderItemVO {
    @ApiModelProperty("商品ID")
    private Long    itemId;
    @ApiModelProperty("skuId")
    private Long    skuId;
    @ApiModelProperty("商品名称")
    private String  itemTitle;
    @ApiModelProperty("SKU名称")
    private String  skuDesc;
    @ApiModelProperty("SKU名称")
    private String  skuName;
    @ApiModelProperty("商家ID")
    private Long    sellerId;
    @ApiModelProperty("商家名称")
    private String  sellerName;
    @ApiModelProperty("商品图片地址")
    private String  picUrl;
    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;
    @ApiModelProperty("下单商品数量")
    private Integer itemQty;
    @ApiModelProperty("最大可售数量")
    private Integer maxSellableQty;
    @ApiModelProperty("商品原价(单价)")
    private Long  originPrice;
    @ApiModelProperty("商品单品折扣价(单价)")
    private Long  itemPrice;
    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long  weight;
    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;
    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;
    @ApiModelProperty(value = "支持的物流信息")
    private List<ItemDeliveryVO> itemDelivery;
    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;
    @ApiModelProperty(value = "订单ID")
    private Long orderId;
    @ApiModelProperty(value = "主订单ID")
    private Long primaryOrderId;
    @ApiModelProperty("商品一口价总金额=itemPrice*itemQty")
    private Long    saleAmt;
    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;
    /**
     *
     */
    @ApiModelProperty("组合商品")
    private List<CombineItemVO> combineItems;

    public String getOriginPriceYuan() {
        return String.valueOf(this.originPrice);
    }

    public String getItemPriceYuan() { return String.valueOf(this.itemPrice); }

    public String getSaleAmtYuan() { return String.valueOf(this.saleAmt);}

    public String getWeightUnit() {
        return ItemUtils.showWeight(weight, Boolean.TRUE);
    }
}
