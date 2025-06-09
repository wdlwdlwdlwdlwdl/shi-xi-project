package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PropValueVO {
    @ApiModelProperty("业务主键")
    private String id;
    @ApiModelProperty("属性项id")
    private Long propId;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("属性值")
    private String value;
}
