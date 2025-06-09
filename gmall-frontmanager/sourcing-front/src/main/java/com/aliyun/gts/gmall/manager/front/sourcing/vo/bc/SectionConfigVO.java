package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("标段打分配置")
public class SectionConfigVO {
    @ApiModelProperty("标段id")
    Long sectionId ;
    @ApiModelProperty("标段名称")
    String sectionName;
    @ApiModelProperty("打分配置")
    DomainVO domains;
}
