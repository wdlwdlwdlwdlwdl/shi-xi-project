package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;

public interface OrderQueryService {

    /**
     * 查询条件生成
     * @param req
     * @return
     */
    TradeBizResult<OrderQueryWrapper> preProcess(CustomerOrderQueryRpcReq req);

    /**
     * 查询订单
     * @param request
     * @return
     */
    TradeBizResult<ListOrder> query(OrderQueryWrapper request);

    /**
     * 查询结果订单
     * @param listOrder
     * @return
     */
    TradeBizResult<PageInfo<MainOrderDTO>> processCustomerResult(ListOrder listOrder);

}
