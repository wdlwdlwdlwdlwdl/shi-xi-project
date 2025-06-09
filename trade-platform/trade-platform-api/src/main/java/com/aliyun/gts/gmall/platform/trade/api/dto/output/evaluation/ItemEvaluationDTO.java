package com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

import java.util.List;

@Data
public class ItemEvaluationDTO extends AbstractOutputInfo {

    /**
     * 商品评价
     */
    private EvaluationDTO itemEvaluation;

    /**
     * 店铺评价
     */
    private EvaluationDTO sellerEvaluation;

    /**
     * 用户追评、卖家回复等, 按时间正序
     */
    private List<EvaluationDTO> extraEvaluation;
}
