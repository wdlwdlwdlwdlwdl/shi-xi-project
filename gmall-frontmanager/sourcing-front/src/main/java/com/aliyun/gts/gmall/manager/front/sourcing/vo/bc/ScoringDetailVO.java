package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("专家打分明细")
public class ScoringDetailVO extends CriterionVO {

    @ApiModelProperty("专家打分")
    Double score;

}
