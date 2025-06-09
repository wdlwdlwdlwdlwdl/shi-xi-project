package com.aliyun.gts.gmall.platform.trade.domain.entity.item;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ItemCategory extends AbstractBusinessEntity {

    @ApiModelProperty("类目id")
    private Long id;

    @ApiModelProperty("类目名称")
    private String name;

    @ApiModelProperty("父类目id，第一层为0")
    private Long parentId;

    @ApiModelProperty("是否叶子类目")
    private Boolean leafYn;

    @ApiModelProperty("状态: 0 屏蔽, 1:启用  ( ItemCategoryStatusEnum )")
    private Integer status;

    @ApiModelProperty("类目特征, 包含从父类目继承的特征")
    private Map<String, String> featureMap;

    @ApiModelProperty(value = "三级类目是否拆单")
    private Boolean thirdCategorySplitOrder;
}
