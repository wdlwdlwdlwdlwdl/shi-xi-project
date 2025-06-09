package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;

public interface LogisticsService {

    TradeBizResult createLogistics(TcLogisticsRpcReq req);

    TradeBizResult updateLogistics(TcLogisticsRpcReq req);
}
