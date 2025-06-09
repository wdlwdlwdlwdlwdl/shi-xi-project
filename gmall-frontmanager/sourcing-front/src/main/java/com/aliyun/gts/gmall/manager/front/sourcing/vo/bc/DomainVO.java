package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("评标维度")
public class DomainVO {

    @ApiModelProperty("评标维度")
    List<String> domainNames;
    @ApiModelProperty("评标标准")
    List<CriterionVO> criterionList;

}
