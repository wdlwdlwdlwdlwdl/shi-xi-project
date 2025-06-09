package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.change;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustDelOrderCheckHandler implements ProcessFlowNodeHandler<Long, Boolean> {

    @Autowired
    OrderStatusService orderStatusService;

    @Override
    public Boolean handleBiz(Map<String, Object> map, Long primaryId) {
        Long custId = (Long) map.get("custId");
        TradeBizResult<Boolean> result = orderStatusService.custDelOrderCheck(primaryId, custId);
        if(result.isSuccess()){
            return result.getData();
        }else{
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL , primaryId , result.getFail().getMessage());
        }
    }
}
