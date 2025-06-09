package com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation;

import lombok.Data;

import java.io.Serializable;

@Data
public class EvaluationRate implements Serializable {

    /** 商家id */
    private Long sellerId;
    /** 评价总数 */
    private Long totalCount;

    /** 1星评价总数 */
    private Long oneLevelCount;

    /** 2星评价总数 */
    private Long twoLevelCount;
    /** 3星评价总数 */
    private Long threeLevelCount;
    /** 4星评价总数 */
    private Long fourLevelCount;
    /** 5星评价总数 */
    private Long fiveLevelCount;
    /** 平均评价 */
    private Double rateScore;
}
