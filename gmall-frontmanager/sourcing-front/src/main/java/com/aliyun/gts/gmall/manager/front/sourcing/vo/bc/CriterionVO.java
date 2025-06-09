package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("评标指标")
public class CriterionVO {

    @ApiModelProperty("维度名称")
    String domainName;
    @ApiModelProperty("指标名称")
    String criterionName;
    @ApiModelProperty("分数上限")
    Double max;
    @ApiModelProperty("评分标准")
    String description;
}
