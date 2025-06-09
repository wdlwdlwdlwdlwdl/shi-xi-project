package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ItemSkuProductOptionVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "类型")
    private String type;
    @ApiModelProperty(value = "属性值")
    private List<ItemSkuProductOptionValueVO> values;
}
