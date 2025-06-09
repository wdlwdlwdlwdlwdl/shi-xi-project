package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.ItemDeliveryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@ApiModel(value = "商品信息")
public class OrderItemDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "商品ID")
    private Long itemId;

    @ApiModelProperty(value = "skuId")
    private Long skuId;

    @ApiModelProperty(value = "商品名称")
    private String itemTitle;

    @ApiModelProperty(value = "SKU名称")
    private String skuDesc;

    @ApiModelProperty(value = "SKU名称")
    private String skuName;

    @ApiModelProperty(value = "商家ID")
    private Long sellerId;

    @ApiModelProperty(value = "商家名称")
    private String sellerName;

    @ApiModelProperty(value = "商品主图地址")
    private String picUrl;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty(value = "下单商品数量")
    private Integer orderQty;

    @ApiModelProperty(value = "最大可售数量")
    private Integer maxSellableQty;

    @ApiModelProperty("单件SKU的重量, 单位g")
    private Long weight;

    @ApiModelProperty(value = "支持的物流方式列表")
    private List<String> supportDeliveryList;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    @ApiModelProperty(value = "支持的物流信息")
    private List<ItemDeliveryDTO> itemDelivery;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;


    // ===== 费用字段 =====

    @ApiModelProperty(value = "商品原价(单价)")
    private Long originPrice;

    @ApiModelProperty(value = "商品营销一口价(单价)")
    private Long itemPrice;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "主订单ID")
    private Long primaryOrderId;

    @ApiModelProperty(value = "扩展信息")
    private Map<String,String> feature;

}
