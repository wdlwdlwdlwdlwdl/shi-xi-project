package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.ItemDeliveryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.ItemDividePromotionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CartItemDTO extends AbstractOutputInfo {

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("sku ID")
    private Long skuId;

    @ApiModelProperty("sku 名称")
    private String skuName;

    @ApiModelProperty("sku描述, 例如 '颜色红色,尺码27' ")
    private String desc;

    @ApiModelProperty("sku库存")
    private Integer skuQty;

    @ApiModelProperty("加购数量")
    private Integer cartQty;

    @ApiModelProperty(value = "单品原价(单价)")
    private Long originPrice;

    @ApiModelProperty(value = "营销一口价(单价)")
    private Long itemPrice;

    @ApiModelProperty("单品折扣价(单价-分)")
    private Long  itemUnitPrice;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty("商品主图")
    private String itemPic;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty("商品状态, ItemStatusEnum")
    private String itemStatus;

    @ApiModelProperty("商家id")
    private Long sellerId;

    @ApiModelProperty("商家名称")
    private String sellerName;

    @ApiModelProperty("商家状态, SellerStatusEnum")
    private String sellerStatus;
    
    @ApiModelProperty("借贷数目")
    private List<Integer> loan;

    @ApiModelProperty("分期数")
    private List<Integer> installment;

    @ApiModelProperty("是否已经查询不到该商品或SKU（删除等）")
    private boolean itemNotFound;

    @ApiModelProperty("营销返回的活动分摊明细")
    private List<ItemDividePromotionDTO> itemDivideDetails;

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

    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    @ApiModelProperty(value = "类目ID")
    private Long categoryId;

    @ApiModelProperty("加购价格")
    private Long addCartPrice;

    @ApiModelProperty("原始的installment分期数")
    private Integer originInstallment;

    @ApiModelProperty("当前的installment分期数")
    private Integer currentInstallment;

    @ApiModelProperty("是否可勾选下单")
    private Boolean selectEnable;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    @ApiModelProperty(value = "支持的物流信息")
    private List<ItemDeliveryDTO> itemDelivery;

    @ApiModelProperty("物流到达预计时间,单位小时,默认-1")
    private int selfTimeliness=-1;

    @ApiModelProperty("分组前支付方式 epay loan_期数 installment_期数")
    private String originalPayMode;

    @ApiModelProperty("购物车ID")
    private Long cartId;

}
