package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationDetailsReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.EvaluationRateVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.EvaluationSumVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.ItemEvaluationVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/11/29 10:55
 */
public interface EvaluationFacade {

    PageInfo<ItemEvaluationVO> queryEvaluationList(EvaluationQueryReq req);

    PageInfo<ItemEvaluationVO> queryList(EvaluationQueryReq req);

    EvaluationDTO detailsEvaluation(EvaluationDetailsReq req);

    EvaluationRateVO rateStatistics(EvaluationReq req);

    EvaluationSumVO ratePicList(EvaluationReq req);

    Boolean isOrderFirstEvaluate(EvaluationReq req);



}
