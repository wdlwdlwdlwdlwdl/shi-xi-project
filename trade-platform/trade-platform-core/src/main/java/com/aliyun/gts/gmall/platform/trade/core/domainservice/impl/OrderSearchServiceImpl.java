package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderStatisticsDTO;
import com.aliyun.gts.gmall.platform.trade.common.domain.KVDO;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchParamAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchResultAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatistics;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderSearchServiceImpl implements OrderSearchService {

    @Autowired
    OrderSearchAbility orderSearchAbility;

    @Autowired
    OrderSearchParamAbility orderSearchParamAbility;

    @Autowired
    OrderSearchResultAbility orderSearchResultAbility;

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req) {
        return orderSearchParamAbility.preProcess(req);
    }

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req) {
        return orderSearchParamAbility.preProcess(req);
    }

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(OrderSearchRpcReq req) {
        return orderSearchParamAbility.preProcess(req);
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req) {
        return orderSearchParamAbility.preEsProcess(req);
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req) {
        return orderSearchParamAbility.preEsProcess(req);
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(OrderSearchRpcReq req) {
        return orderSearchParamAbility.preEsProcess(req);
    }

    @Override
    public TradeBizResult<ListOrder> search(SimpleSearchRequest request) {
        return orderSearchAbility.search(request);
    }

    @Override
    public TradeBizResult<ListOrder> search(SearchRequest request) {
        return orderSearchAbility.search(request);
    }

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processCustomerResult(ListOrder listOrder) {
        return orderSearchResultAbility.processCustResult(listOrder);
    }

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processSellerResult(ListOrder listOrder) {
        return orderSearchResultAbility.processResult(listOrder);
    }

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processAdminResult(ListOrder listOrder) {
        return orderSearchResultAbility.processAdminResult(listOrder);
    }

    @Override
    public Map<Integer, Integer> countByStatus(CountOrderQueryRpcReq req) {
        List<KVDO<Integer, Integer>> list = tcOrderRepository.countByStatus(req.getCustId(), req.getStatus());
        Map<Integer, Integer> map = Maps.newHashMap();
        for (Integer status : req.getStatus()) {
            map.put(status, 0);
        }
        if (list != null) {
            for (KVDO<Integer, Integer> kvdo : list) {
                map.put(kvdo.getKey(), kvdo.getValue());
            }
        }
        return map;
    }

    @Override
    public List<OrderStatisticsDTO> statisticsBySellerIds(OrderStatisticsQueryRpcReq req) {
        List<OrderStatistics> statisticsList = tcOrderRepository.statisticsBySellerIds(req.getSellerIds(), req.getOrderStatus());
        return  statisticsList.stream().map(target -> {
            OrderStatisticsDTO source = new OrderStatisticsDTO();
            source.setSellerId(target.getSellerId());
            source.setTotalCount(target.getTotalCount());
            source.setTotalCountLast30Days(target.getTotalCountLast30Days());
            source.setTotalCompleteCountLast30Days(target.getTotalCompleteCountLast30Days());
            source.setCancelCount(target.getCancelCount());
            source.setCancelCountLast30Days(target.getCancelCountLast30Days());
            return source;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderStatisticsDTO> statisticsBySeller(OrderStatisticsQueryRpcReq req) {
        log.info("[查询订单数量]状态={}, 商家id={}", req.getOrderStatus(), req.getSellerIds().stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]")));
        List<OrderStatistics> statisticsList = tcOrderRepository.statisticsBySeller(req.getSellerIds());
        log.info("[查询订单数量]查询结果={}", JSON.toJSONString(statisticsList));
        return  statisticsList.stream().map(target -> {
            OrderStatisticsDTO source = new OrderStatisticsDTO();
            source.setSellerId(target.getSellerId());
            source.setTotalCount(target.getTotalCount());
            return source;
        }).collect(Collectors.toList());
    }

}
