package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CustDeliveryFeeHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.DeliveryFeeHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CustDeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class CustDeliveryFeeHistoryFacadeImpl implements CustDeliveryFeeHistoryFacade {

    @Autowired
    private CustDeliveryFeeHistoryService custDeliveryFeeHistoryService;

    @Override
    public RpcResponse<List<CustDeliveryFeeHistoryDTO>> queryCustDeliveryFee(CustDeliveryFeeHistoryRpcReq req) {
        return RpcResponse.ok(custDeliveryFeeHistoryService.queryCustDeliveryFeeHistory(req));
    }

    @Override
    public RpcResponse<CustDeliveryFeeHistoryDTO> saveCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req) {
        return RpcResponse.ok(custDeliveryFeeHistoryService.saveCustDeliveryFeeHistory(req));
    }
}
