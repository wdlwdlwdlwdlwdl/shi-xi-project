package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.logistics.LogisticsParamAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogisticsServiceImpl implements LogisticsService {

    @Autowired
    LogisticsParamAbility logisticsParamAbility;

    @Autowired
    TcLogisticsRepository tcLogisticsRepository;

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Override
    public TradeBizResult createLogistics(TcLogisticsRpcReq req) {
        TradeBizResult<List<TcLogisticsDO>> result = logisticsParamAbility.preProcess(req);
        if(result.isSuccess()){
            tcLogisticsRepository.create(result.getData());
            return TradeBizResult.ok();
        }else{
            return TradeBizResult.fail(result.getFail());
        }
    }

    @Override
    public TradeBizResult updateLogistics(TcLogisticsRpcReq req) {
        TcLogisticsDO logisticsDO = tcLogisticsRepository.queryLogisticsByPrimaryId(req.getPrimaryOrderId(),null);
        if(logisticsDO == null){
            return TradeBizResult.fail(
                OrderErrorCode.ORDER_NOT_EXISTS.getCode(),
                OrderErrorCode.ORDER_NOT_EXISTS.getMessage()
            );
        }
        logisticsDO.setPrimaryOrderId(req.getPrimaryOrderId());
        if(req.getOtpCode() != null){
            logisticsDO.setOtpCode(req.getOtpCode());
        }
        if(req.getReturnCode() != null){
            logisticsDO.setReturnCode(req.getReturnCode());
        }
        if(req.getLogisticsUrl() != null){
            logisticsDO.setLogisticsUrl(req.getLogisticsUrl());
        }
        if(req.getLogisticsId() != null){
            logisticsDO.setLogisticsId(req.getLogisticsId());
        }
        if(req.getDeliveryServiceName() != null){
            logisticsDO.setCompanyName(req.getDeliveryServiceName());
        }
        tcLogisticsRepository.update(logisticsDO);
        return TradeBizResult.ok();
    }
}
