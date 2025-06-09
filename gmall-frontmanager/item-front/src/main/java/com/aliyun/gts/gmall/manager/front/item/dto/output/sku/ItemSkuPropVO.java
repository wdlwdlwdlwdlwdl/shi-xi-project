package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品的SKU属性
 *
 * @author tiansong
 */
@Data
@ApiModel("商品的SKU属性")
public class ItemSkuPropVO extends AbstractOutputInfo {
    @ApiModelProperty("商品的SKU属性项ID")
    private Long pid;
    @ApiModelProperty("商品的SKU属性项名称")
    private String name;
    @ApiModelProperty("商品的SKU属性值列表")
    private List<ItemSkuValueVO> valueList;
}
