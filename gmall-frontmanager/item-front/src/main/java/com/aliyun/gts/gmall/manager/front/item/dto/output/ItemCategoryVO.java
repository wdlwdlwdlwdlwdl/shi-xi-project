package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品类目信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品类目信息")
public class ItemCategoryVO extends AbstractOutputInfo {
    @ApiModelProperty("分类id")
    private Long id;
    @ApiModelProperty("分类名称")
    private String name;
    @ApiModelProperty("父分类id，第一层为0")
    private Long parentId;
    @ApiModelProperty("是否叶子类目")
    private Boolean leafYn;
}
