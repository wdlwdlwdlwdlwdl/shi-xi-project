package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderStatusInfo;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderQueryParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class DefaultOrderQueryParamExt implements OrderQueryParamExt {
    @Override
    public TradeBizResult<OrderQueryWrapper> preProcess(CustomerOrderQueryRpcReq req) {
        OrderQueryWrapper queryWrapper = new OrderQueryWrapper();
        Set<Integer> statusList = null;
        if (req.getStatusList() != null) {
            statusList = new HashSet<>();
            for (OrderStatusInfo status : req.getStatusList()) {
                statusList.add(status.getOrderStatus());
            }
            req.setStatusList(null);
        }
        BeanUtils.copyProperties(req , queryWrapper);
        queryWrapper.setStatusList(statusList);
        return TradeBizResult.ok(queryWrapper);
    }
}
