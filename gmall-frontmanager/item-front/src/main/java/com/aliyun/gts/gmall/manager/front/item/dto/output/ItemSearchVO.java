package com.aliyun.gts.gmall.manager.front.item.dto.output;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品搜索信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品搜索信息")
public class ItemSearchVO {
    @ApiModelProperty("标签，多值 \t分隔")
    private String bizTag;
    @ApiModelProperty("最近30天销售量")
    private String saleCount30;
    @ApiModelProperty("总体销售量")
    private String saleCountAll;
    @ApiModelProperty("评价分")
    private String evaluationScore;
}
