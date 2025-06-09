package com.aliyun.gts.gmall.test.platform.ext;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchParamExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchResultExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderSearchExtTest extends BaseTest {

    @Autowired
    OrderSearchParamExt orderSearchParamExt;

    @Autowired
    OrderSearchExt orderSearchExt;

    @Autowired
    OrderSearchResultExt orderSearchResultExt;

    @Test
    @Ignore
    public void testCustSearch(){
        CustomerOrderQueryRpcReq customerOrderQueryRpcReq = new CustomerOrderQueryRpcReq();
        customerOrderQueryRpcReq.setStatus(20);
        customerOrderQueryRpcReq.setPageSize(0);
        TradeBizResult<SimpleSearchRequest> r1 = orderSearchParamExt.preProcess(customerOrderQueryRpcReq);

        TradeBizResult<ListOrder> r2 = orderSearchExt.search(r1.getData());

        TradeBizResult<PageInfo<MainOrderDTO>> r3 = orderSearchResultExt.processResult(r2.getData());

        System.out.println();
    }

}
