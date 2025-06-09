package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemSkuProductOptionValueVO {
    @ApiModelProperty(value = "属性值名称")
    private String name;
    @ApiModelProperty(value = "属性值")
    private String value;
    @ApiModelProperty(value = "地址")
    private String url;
}
