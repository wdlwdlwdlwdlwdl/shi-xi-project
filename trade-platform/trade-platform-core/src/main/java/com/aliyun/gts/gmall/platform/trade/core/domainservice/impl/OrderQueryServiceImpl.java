package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryParamAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryResultAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderQueryService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    @Autowired
    OrderQueryAbility orderQueryAbility;

    @Autowired
    OrderQueryParamAbility orderQueryParamAbility;

    @Autowired
    OrderQueryResultAbility orderQueryResultAbility;

    /**
     * 查询条件生成
     * @param req
     * @return
     */
    @Override
    public TradeBizResult<OrderQueryWrapper> preProcess(CustomerOrderQueryRpcReq req) {
        return orderQueryParamAbility.preProcess(req);
    }

    /**
     * 查询订单
     * @param request
     * @return
     */
    @Override
    public TradeBizResult<ListOrder> query(OrderQueryWrapper request) {
        return orderQueryAbility.query(request);
    }

    /**
     * 查询结果订单
     * @param listOrder
     * @return
     */
    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processCustomerResult(ListOrder listOrder) {
        return orderQueryResultAbility.processResult(listOrder);
    }
}
