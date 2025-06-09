package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.nacos.shaded.com.google.common.reflect.TypeToken;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.consts.PageParamConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.convertor.EvaluationConverter;
import com.aliyun.gts.gmall.manager.front.trade.convertor.OrderConverter;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationDetailsReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EvaluationReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.facade.EvaluationFacade;
import com.aliyun.gts.gmall.manager.front.trade.util.ErrorCodeUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationIdReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRateRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderBatchQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationRateDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationRatePicDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.ItemEvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.EvaluationApproveStatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/11/29 10:55
 */
@Slf4j
@Service
public class EvaluationFacadeImpl implements EvaluationFacade {

    @Autowired
    private EvaluationReadFacade evaluationReadFacade;
    @Autowired
    private EvaluationWriteFacade evaluationWriteFacade;
    @Autowired
    private OrderReadFacade orderReadFacade;
    @Autowired
    private EvaluationConverter evaluationConverter;
    @Autowired
    private OrderConverter orderConverter;

    @Override
    public PageInfo<ItemEvaluationVO> queryEvaluationList(EvaluationQueryReq req) {
        if (UserHolder.getUser() != null) {
            req.setCustId(UserHolder.getUser().getCustId());
        }//我的评价查询 独立出来
//        req.setStatus(String.valueOf(EvaluationApproveStatusEnum.PASSED.getCode()));
        req.build();
        RpcResponse<PageInfo<ItemEvaluationDTO>> resp = evaluationReadFacade.queryEvaluation(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapFailInfo(resp.getFail()), null);
        }
        PageInfo<ItemEvaluationVO> page = evaluationConverter.convertItemEvaluationPage(resp.getData());
        fillOrderInfo(page.getList());
        return page;
    }

    @Override
    public PageInfo<ItemEvaluationVO> queryList(EvaluationQueryReq req) {
        req.setStatus(String.valueOf(EvaluationApproveStatusEnum.PASSED.getCode()));
        req.setHasItemId(true);
        req.build();
        RpcResponse<PageInfo<ItemEvaluationDTO>> resp = evaluationReadFacade.queryEvaluation(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapFailInfo(resp.getFail()), null);
        }
        PageInfo<ItemEvaluationVO> page = evaluationConverter.convertItemEvaluationPage(resp.getData());
        fillOrderInfo(page.getList());
        return page;
    }

    @Override
    public EvaluationDTO detailsEvaluation(EvaluationDetailsReq req) {
        OrderInfo order = getOrderInfo(req);

        EvaluationIdReq evaluationIdReq = new EvaluationIdReq();
        evaluationIdReq.setEvaluationId(req.getEvaluationId());
        evaluationIdReq.setPrimaryOrderId(req.getPrimaryOrderId());
        evaluationIdReq.setCustId(req.getCustId());
        RpcResponse<EvaluationDTO> evaluationDTORpcResponse = evaluationReadFacade.querySingleById(evaluationIdReq);
        return evaluationDTORpcResponse.getData();
    }

    @Override
    public EvaluationRateVO rateStatistics(EvaluationReq req) {
        EvaluationRateRpcReq rpc = new EvaluationRateRpcReq();
        rpc.setSellerId(req.getSellerId());
        rpc.setSkuId(req.getSkuId());
        RpcResponse<EvaluationRateDTO> resp = evaluationReadFacade.rateStatistics(rpc);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapFailInfo(resp.getFail()), null);
        }
        return evaluationConverter.convertEvaluation(resp.getData());
    }

    @Override
    public EvaluationSumVO ratePicList(EvaluationReq req) {
        EvaluationRateRpcReq rpc = new EvaluationRateRpcReq();
        rpc.setSellerId(req.getSellerId());
        rpc.setSkuId(req.getSkuId());
        rpc.setItemId(req.getItemId());
        EvaluationSumVO vo = new EvaluationSumVO();
        RpcResponse<List<EvaluationRatePicDTO>> ratePicList = evaluationReadFacade.ratePicList(rpc);
        List<EvaluationRatePicVO> rateList = new ArrayList<>();

        if (ratePicList.isSuccess()) {
            for (EvaluationRatePicDTO evaluationRatePicDTO : ratePicList.getData()) {
                if (!evaluationRatePicDTO.getRatePic().isEmpty()) {
                    for (String pic : evaluationRatePicDTO.getRatePic()) {
                        EvaluationRatePicVO picVo = new EvaluationRatePicVO();
                        BeanUtils.copyProperties(evaluationRatePicDTO, picVo);
                        picVo.setPicUrl(pic);
                        rateList.add(picVo);
                    }
                }
            }
            vo.setRateList(rateList);
            vo.setTotalCount(rateList.size());
            sortList(rateList, vo);
        }
        return vo;
    }

    /**
     * 是否首次评论
     *
     * @param req
     * @return
     */
    @Override
    public Boolean isOrderFirstEvaluate(EvaluationReq req) {
        EvaluationQueryReq queryReq = new EvaluationQueryReq();
        if (UserHolder.getUser() != null) {
            queryReq.setCustId(UserHolder.getUser().getCustId());
        }
//        if (null == req.getPrimaryOrderId()) {
//            throw new GmallException(ErrorCodeUtils.mapFailInfo(FailInfo.builder().message("primaryOrderId can not be null").build()), null);
//        }
        queryReq.setPrimaryOrderId(req.getPrimaryOrderId());
        PageParam page = new PageParam(PageParamConst.DEFAULT_PAGE_NO, PageParam.DEFAULT_PAGE_SIZE);
        queryReq.setPage(page);
        RpcResponse<PageInfo<ItemEvaluationDTO>> resp = evaluationReadFacade.queryEvaluation(queryReq);
        PageInfo<ItemEvaluationDTO> pageInfo = resp.getData();
        return CollectionUtils.isEmpty(pageInfo.getList());
    }

    private static void sortList(List<EvaluationRatePicVO> rateList, EvaluationSumVO vo) {
        List<EvaluationRatePicVO> ngList = new ArrayList<>();
        List<EvaluationRatePicVO> okList = new ArrayList<>();
        for (EvaluationRatePicVO evaluationRatePicVO : rateList) {
            if (evaluationRatePicVO.getRateScore() == 4 || evaluationRatePicVO.getRateScore() == 5) {
                okList.add(evaluationRatePicVO);
            } else {
                ngList.add(evaluationRatePicVO);
            }
        }
        vo.setNgList(ngList);
        vo.setNgCount(ngList.size());
        vo.setOkList(okList);
        vo.setOkCount(okList.size());
    }

    private OrderInfo getOrderInfo(EvaluationDetailsReq req) {
        OrderDetailQueryRpcReq detailReq = new OrderDetailQueryRpcReq();
        detailReq.setPrimaryOrderId(req.getPrimaryOrderId());
        RpcResponse<MainOrderDetailDTO> resp = orderReadFacade.queryOrderDetail(detailReq);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapFailInfo(resp.getFail()), null);
        }
        MainOrderDetailDTO detail = resp.getData();
        SubOrderDetailDTO subOrder = detail.getSubDetailOrderList().stream()
                .filter(sub -> sub.getOrderId().longValue() == req.getOrderId().longValue())
                .findFirst().orElse(null);
        if (subOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        return new OrderInfo(detail, subOrder);
    }

    private void fillOrderInfo(List<ItemEvaluationVO> list) {
        Set<Long> primaryOrderIds = new HashSet<>();
        for (ItemEvaluationVO ev : list) {
            primaryOrderIds.add(ev.getItemEvaluation().getPrimaryOrderId());
            // 筛选出只审批通过的回复
            List<ItemEvaluationVO.EvaluationVO> evaluationVOList = ev.getExtraEvaluation().stream().filter(a -> {
                Map extend = a.getExtend();
                Integer code = extend.get("evaluationApproveStatus") == null ? null : Integer.valueOf(extend.get("evaluationApproveStatus").toString());
                return EvaluationApproveStatusEnum.PASSED.getCode().equals(code);
            }).collect(Collectors.toList());
            ev.setExtraEvaluation(evaluationVOList);
        }

        OrderBatchQueryRpcReq req = new OrderBatchQueryRpcReq();
        req.setPrimaryOrderIds(new ArrayList<>(primaryOrderIds));
        RpcResponse<List<MainOrderDTO>> resp = orderReadFacade.batchQueryOrderInfo(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorCodeUtils.mapFailInfo(resp.getFail()), null);
        }

        Map<Long, MainOrderDTO> mainOrderMap = new HashMap<>();
        Map<Long, SubOrderDTO> subOrderMap = new HashMap<>();
        for (MainOrderDTO main : resp.getData()) {
            mainOrderMap.put(main.getOrderId(), main);
            for (SubOrderDTO sub : main.getSubOrderList()) {
                subOrderMap.put(sub.getOrderId(), sub);
            }
        }

        for (ItemEvaluationVO ev : list) {
            SubOrderDTO sub = subOrderMap.get(ev.getItemEvaluation().getOrderId());
            MainOrderDTO main = mainOrderMap.get(ev.getItemEvaluation().getPrimaryOrderId());
            if (main == null || sub == null) {
                continue;
            }
            SubOrderVO orderInfo = orderConverter.build(sub, main);
            ev.setSubOrderInfo(orderInfo);
        }
    }

    @AllArgsConstructor
    private static class OrderInfo {
        MainOrderDTO main;
        SubOrderDTO sub;
    }

}
