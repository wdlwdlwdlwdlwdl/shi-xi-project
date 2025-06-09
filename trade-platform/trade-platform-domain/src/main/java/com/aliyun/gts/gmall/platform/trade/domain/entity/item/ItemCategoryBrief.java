package com.aliyun.gts.gmall.platform.trade.domain.entity.item;

import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ItemCategoryBrief extends AbstractBusinessEntity {

    @ApiModelProperty("类目id")
    private Long id;

    @ApiModelProperty("类目名称")
    private MultiLangText name;

    @ApiModelProperty("父类目id，第一层为0")
    private Long parentId;

    @ApiModelProperty("是否叶子类目")
    private Boolean leafYn;
}
