package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import lombok.Data;

import java.util.List;

@Data
public class ItemEvaluationVO {

    /**
     * 商品评价
     */
    private EvaluationVO itemEvaluation;

    /**
     * 店铺评价
     */
    private EvaluationVO sellerEvaluation;

    /**
     * 追评、卖家回复等
     */
    private List<EvaluationVO> extraEvaluation;

    /**
     * 订单信息
     */
    private SubOrderVO subOrderInfo;


    public static class EvaluationVO extends EvaluationDTO {

    }
}
