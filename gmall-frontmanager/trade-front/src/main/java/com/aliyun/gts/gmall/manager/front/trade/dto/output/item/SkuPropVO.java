package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SKU的属性值
 *
 * @author tiansong
 */
@Data
@ApiModel("SKU的属性值")
public class SkuPropVO extends AbstractOutputInfo {
    @ApiModelProperty("SKU的属性项ID")
    private Long pid;
    @ApiModelProperty("SKU的属性值ID")
    private String vid;
}
