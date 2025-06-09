package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SubOrderVO extends OrderVO{

    @ApiModelProperty("商品数量")
    Integer itemQuantity;

    @ApiModelProperty("商品名称")
    String itemTitle;

    @ApiModelProperty("sku描述")
    String skuDesc;

    @ApiModelProperty("商品扩展")
    String itemFeature;

    @ApiModelProperty("商品主图")
    String itemPic;

    @ApiModelProperty("SKU图片, 如没有SKU图片则为空")
    private String skuPic;

    @ApiModelProperty("商品id")
    Long itemId;

    @ApiModelProperty("skuid")
    Long skuId;

    @ApiModelProperty("售后状态")
    String reversalStatus;


}
