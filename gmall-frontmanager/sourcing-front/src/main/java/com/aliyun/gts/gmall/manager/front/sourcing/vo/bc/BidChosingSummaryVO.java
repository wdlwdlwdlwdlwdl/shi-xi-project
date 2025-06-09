package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("评标打分概览 单个标的所有专家的打分情况")
@Data
public class BidChosingSummaryVO {

    Long sectionId;

    String sectionName;

    List<BidChosingCompanySummaryVO> summaryList;


}
