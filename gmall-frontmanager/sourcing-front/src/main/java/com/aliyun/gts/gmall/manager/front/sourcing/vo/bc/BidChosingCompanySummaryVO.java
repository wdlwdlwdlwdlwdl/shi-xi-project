package com.aliyun.gts.gmall.manager.front.sourcing.vo.bc;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("单个公司在单个报价单上的所有专家打分")
@Data
public class BidChosingCompanySummaryVO {

    Long companyId;

    Long supplierId;

    String companyName;

    Double totalScore;

    Boolean selected;

    Long quoteId;

    List<BidChosingDetailSummaryVO> scoringSummaryList;

    public void calcTotal(){
        if(scoringSummaryList != null){
            totalScore = scoringSummaryList.stream().mapToDouble(e->e.getAverage()).summaryStatistics().getSum();
        }
    }


}
