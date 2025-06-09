package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EvaluationSumVO {

    private int totalCount;

    private int okCount;

    private int ngCount;

    private List<EvaluationRatePicVO> rateList;

    private List<EvaluationRatePicVO> okList;

    private List<EvaluationRatePicVO> ngList;
}
