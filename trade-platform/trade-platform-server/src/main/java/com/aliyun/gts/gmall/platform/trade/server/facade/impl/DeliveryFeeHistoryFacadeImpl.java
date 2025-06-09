package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.DeliveryFeeHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonHistoryService;
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
public class DeliveryFeeHistoryFacadeImpl implements DeliveryFeeHistoryFacade {

    @Autowired
    private DeliveryFeeHistoryService deliveryFeeHistoryService;

    @Override
    public RpcResponse<List<DeliveryFeeHistoryDTO>> queryDeliveryFee(DeliveryFeeHistoryRpcReq req) {
        return RpcResponse.ok(deliveryFeeHistoryService.queryDeliveryFeeHistory(req));
    }

    @Override
    public RpcResponse<DeliveryFeeHistoryDTO> saveDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req) {
        return RpcResponse.ok(deliveryFeeHistoryService.saveDeliveryFeeHistory(req));
    }
}
