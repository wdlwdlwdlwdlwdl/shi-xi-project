package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("评标打分详情 单个标段 的单个专家的打分情况")
@Data
public class BidChosingDetailVO {

    @ApiModelProperty("标段id")
    Long sectionId;
    @ApiModelProperty("标段名称")
    String sectionName;
    @ApiModelProperty("打分详情")
    List<SectionDetailVO> detailList;
    @ApiModelProperty("评标意见")
    String description;
    @ApiModelProperty("评标意见id")
    Long opinionId;
    @ApiModelProperty("0草稿 1生效")
    Integer status;

}
