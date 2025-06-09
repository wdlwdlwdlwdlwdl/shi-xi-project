package com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

@Data
public class EvaluationRateDTO extends AbstractOutputInfo {

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
