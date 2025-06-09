package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 10:57 AM
 */
@Getter
@Setter
@ToString
@ApiModel("地址信息")
public class RegionVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("parentId")
    private Long parentId;

    @ApiModelProperty("层级")
    private Integer level;

    @ApiModelProperty("名称")
    private String name;

}
