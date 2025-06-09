package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SubOrderDTO extends BaseOrderDTO{

    @ApiModelProperty("主订单id")
    Long primaryOrderId;

    @ApiModelProperty("商品名称")
    String itemTitle;

    @ApiModelProperty("sku描述")
    String skuDesc;

    @ApiModelProperty("商品扩展")
    String categoryId;

    @ApiModelProperty("商品主图")
    String itemPic;

    @ApiModelProperty("是否可以退")
    Integer canRefunds;

    @ApiModelProperty("类目")
    String categoryName;

    @ApiModelProperty("商品类型")
    Long itemType;

    //
    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty("商品id")
    Long itemId;

    @ApiModelProperty("skuid")
    Long skuId;

    @ApiModelProperty("sellerId")
    Long sellerId;

    @ApiModelProperty("单件SKU的重量, 单位g")
    Long weight;

    @ApiModelProperty("商品分摊营销活动明细")
    List<ItemDividePromotionDTO> itemDividePromotions;

    @ApiModelProperty(value = "扩展信息;目前存放组合商品信息")
    private Map<String,String> feature;

    EvaluationDTO evaluationDTO = new EvaluationDTO();
}
