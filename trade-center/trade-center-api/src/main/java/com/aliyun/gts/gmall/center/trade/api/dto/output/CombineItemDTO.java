package com.aliyun.gts.gmall.center.trade.api.dto.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author haibin.xhb
 * @description: TODO 组合商品SKU
 * @date 2021/10/21 17:55
 */
@Data
public class CombineItemDTO implements Serializable {

    @ApiModelProperty("skuID")
    private Long skuId;

    @ApiModelProperty("商品ID")
    private Long itemId;

    @ApiModelProperty("组合数量")
    private Integer joinQty;

    @ApiModelProperty("商品主图")
    private String itemPic;

    @ApiModelProperty("价格信息")
    private Long itemPrice;

    @ApiModelProperty("sku描述, 例如 '颜色红色,尺码27' ")
    private String skuDesc;

    @ApiModelProperty("商品标题")
    private String itemTitle;

    @ApiModelProperty(value = "商品类型, IC字段")
    private Integer itemType;

    @ApiModelProperty("sku库存")
    private Integer skuQty;
}
