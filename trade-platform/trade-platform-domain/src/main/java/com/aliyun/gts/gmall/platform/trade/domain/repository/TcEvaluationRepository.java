package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.Evaluation;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.EvaluationRate;

import java.util.Collection;
import java.util.List;

/**
 * Created by auto-generated on 2021/02/04.
 */
public interface TcEvaluationRepository{

    List<TcEvaluationDO> batchQueryByPrimaryOrderId(Collection<Long> primaryOrderIds);

    List<TcEvaluationDO> batchSubOrderByPrimaryOrderId(Collection<Long> primaryOrderIds);

    List<TcEvaluationDO> queryByPrimaryOrderId(Long primaryOrderId);

    TcEvaluationDO queryById(Long id, Long primaryOrderId);

    List<TcEvaluationDO> getEvaluationWithReplies(Long primaryOrderId);

    TcEvaluationDO create(TcEvaluationDO tcEvaluationDO);

    // 根据 primary_order_id + id + gmt_modified 更新
    boolean update(TcEvaluationDO tcEvaluationDO);

    int delete(Long id);

    EvaluationRate statisticsRateBySeller(Evaluation evaluation);

    List<TcEvaluationDO> queryRatePicList(Evaluation evaluation);

    List<TcEvaluationDO> getEvaluationList(Evaluation evaluation);
}
