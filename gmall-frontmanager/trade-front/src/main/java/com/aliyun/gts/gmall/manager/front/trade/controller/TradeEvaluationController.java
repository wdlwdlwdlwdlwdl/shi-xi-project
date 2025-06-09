package com.aliyun.gts.gmall.manager.front.trade.controller;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationDetailsReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.EvaluationRateVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.EvaluationSumVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.ItemEvaluationVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.EvaluationFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评价查询
 * @author yangl
 */
@Slf4j
@RestController
@RequestMapping(value = "/trade/evaluation")
public class TradeEvaluationController{

    @Autowired
    private EvaluationFacade evaluationFacade;

    @RequestMapping(value = "/search")
    public RestResponse<PageInfo<ItemEvaluationVO>> queryEvaluationList(@RequestBody EvaluationQueryReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.queryEvaluationList(req));
    }

    @RequestMapping(value = "/query")
    public RestResponse<PageInfo<ItemEvaluationVO>> queryList(@RequestBody EvaluationQueryReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.queryList(req));
    }

    @RequestMapping(value = "/details")
    public RestResponse<EvaluationDTO> detailsEvaluation(@RequestBody EvaluationDetailsReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.detailsEvaluation(req));
    }

    @RequestMapping(value = "/rateStatistics")
    public RestResponse<EvaluationRateVO> rateStatistics(@RequestBody EvaluationReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.rateStatistics(req));
    }

    @RequestMapping(value = "/ratePic")
    public RestResponse<EvaluationSumVO> ratePicList(@RequestBody EvaluationReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.ratePicList(req));
    }

    /**
     * 是否第一次首评
     * @param req
     * @return
     */
    @RequestMapping(value = "/isOrderFirstEvaluate")
    public RestResponse<Boolean> isOrderFirstEvaluation(@RequestBody EvaluationReq req) {
        return RestResponse.okWithoutMsg(evaluationFacade.isOrderFirstEvaluate(req));
    }

}
