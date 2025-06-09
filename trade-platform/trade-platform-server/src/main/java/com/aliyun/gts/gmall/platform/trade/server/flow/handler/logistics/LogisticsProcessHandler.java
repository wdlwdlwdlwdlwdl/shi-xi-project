package com.aliyun.gts.gmall.platform.trade.server.flow.handler.logistics;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogisticsProcessHandler implements
    ProcessFlowNodeHandler<TcLogisticsRpcReq, Boolean> {

    @Autowired
    LogisticsService logisticsService;

    @Override
    public Boolean handleBiz(Map<String, Object> map, TcLogisticsRpcReq req) {
        TradeBizResult result = logisticsService.createLogistics(req);
        if(result.isSuccess()){
            return true;
        }else{
            throw new GmallException(OrderErrorCode.LOGISTICS_CREATE_ERROR , JSON.toJSONString(req) ,
                result.getFail().getMessage());
        }
    }
}
