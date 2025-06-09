package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 3/8/21 10:52 AM
 */
@Getter
@Setter
@ToString
public class DictVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("字典key")
    private String dictKey;

    @ApiModelProperty("字典key的备注")
    private String remark;

    @ApiModelProperty("字典的value, json字符串")
    private String dictValue;

    @ApiModelProperty("类型，1 前端渲染tcode，2 通用")
    private Integer type;

    @ApiModelProperty("备注")
    private String features;

}
