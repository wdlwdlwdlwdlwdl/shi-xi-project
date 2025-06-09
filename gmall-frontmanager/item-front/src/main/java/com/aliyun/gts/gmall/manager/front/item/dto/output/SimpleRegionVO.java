package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 5:27 PM
 */
@Getter
@Setter
@ToString
public class SimpleRegionVO {
    @NotNull
    @ApiModelProperty("地址id")
    private Long                 id;
    @NotNull
    @ApiModelProperty("名称")
    private String               name;
    @NotNull
    @ApiModelProperty("下一级地址列表")
    private List<SimpleRegionVO> children;
}
