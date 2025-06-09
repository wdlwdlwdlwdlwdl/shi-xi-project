package com.aliyun.gts.gmall.manager.front.item.dto.output;

import javax.validation.constraints.NotNull;

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

    @NotNull
    @ApiModelProperty("id")
    private Long id;

    @NotNull
    @ApiModelProperty("parentId")
    private Long parentId;

    @NotNull
    @ApiModelProperty("层级")
    private Integer level;

    @NotNull
    @ApiModelProperty("名称")
    private String name;

}
