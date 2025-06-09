package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("标段打分详情")
public class SectionDetailVO {
    @ApiModelProperty("投标id")
    Long quoteId ;
    @ApiModelProperty("寻源id")
    Long sourcingId;
    @ApiModelProperty("配置id")
    Long configId;
    @ApiModelProperty("supplierId  供应商id")
    Long companyId;
    @ApiModelProperty("公司名称")
    String companyName;
    @ApiModelProperty("打分明细")
    List<ScoringDetailVO> scoringDetailList;

}
