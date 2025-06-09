package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SellerOrderSearchResultHandler implements ProcessFlowNodeHandler<ListOrder, FlowNodeResult> {

    @Autowired
    OrderSearchService orderSearchService;

    @Override
    public FlowNodeResult handleBiz(Map<String, Object> map, ListOrder listOrder) {
        TradeBizResult<PageInfo<MainOrderDTO>> result = orderSearchService.processSellerResult(listOrder);
        if(result.isSuccess()){
            return FlowNodeResult.ok(result.getData());
        }else{
            return FlowNodeResult.fail(result.getFail());
        }
    }
}
