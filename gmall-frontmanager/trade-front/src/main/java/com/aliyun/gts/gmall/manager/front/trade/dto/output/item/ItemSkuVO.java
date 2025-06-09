package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SKU信息
 *
 * @author tiansong
 */
@Data
@ApiModel("SKU信息")
public class ItemSkuVO extends AbstractOutputInfo {
    @ApiModelProperty("skuId")
    private Long id;
    @ApiModelProperty("商品id")
    private Long itemId;
    @ApiModelProperty("库存")
    private Integer quantity;
    @ApiModelProperty("价格,分")
    private Integer price;
    @ApiModelProperty("重量,kg")
    private Integer weight;
    @ApiModelProperty("sku属性信息")
    private List<SkuPropVO> skuPropList;
}
