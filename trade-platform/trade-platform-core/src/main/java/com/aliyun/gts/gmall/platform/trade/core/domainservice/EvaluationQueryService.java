package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationBatchQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationIdReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRateRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.*;

import java.util.List;

public interface EvaluationQueryService {

    /**
     * 评价查询
     */
    PageInfo<ItemEvaluationDTO> queryEvaluation(EvaluationQueryRpcReq req);

    /**
     * admin、seller 评论分页列表
     *
     * @param req
     * @return
     */
    PageInfo<ItemEvaluationV2DTO> queryEvaluationV2(EvaluationQueryRpcReq req);



    /**
     * 评价条数查询
     */
    Integer queryEvaluationCount(EvaluationQueryRpcReq req);

    /**
     * 批量 评价条数查询
     */
    List<Integer> batchQueryEvaluationCount(EvaluationBatchQueryRpcReq req);

    EvaluationDTO querySingleById(EvaluationIdReq req);

    /**
     * 查询评论和追评数据
     */
    List<EvaluationDTO> getEvaluationWithReplies(EvaluationIdReq req);

    /**
     * 查询商户商品评价统计
     */
    EvaluationRateDTO rateStatistics(EvaluationRateRpcReq req);
    /**
     * 查询商品图片
     */
    List<EvaluationRatePicDTO> ratePicList(EvaluationRateRpcReq req);

    /**
     * 查询评论和追评数据
     */
    List<EvaluationDTO> getEvaluationList(EvaluationIdReq req);
}
