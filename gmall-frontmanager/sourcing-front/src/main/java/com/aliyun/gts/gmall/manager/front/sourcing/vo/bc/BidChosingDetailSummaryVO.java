package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("评标打分概览 单个标的单个指标所有专家的打分情况")
@Data
public class BidChosingDetailSummaryVO {

    String domainName;

    String criterionName;

    Double average;

    Double max;

    List<ExpertScoringDisplayVO> expertScoring;

    public void calcAverage(){
        if(expertScoring != null){
            average = expertScoring.stream().mapToDouble(e->e.getScore()).summaryStatistics().getAverage();
        }
    }
}
