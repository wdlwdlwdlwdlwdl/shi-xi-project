package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.DeliveryFeeHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.FeeRulesHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.FeeRulesHistoryService;
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
public class FeeRulesHistoryFacadeImpl implements FeeRulesHistoryFacade {

    @Autowired
    private FeeRulesHistoryService feeRulesHistoryService;

    @Override
    public RpcResponse<List<FeeRulesHistoryDTO>> queryFeeRules(FeeRulesHistoryRpcReq req) {
        return RpcResponse.ok(feeRulesHistoryService.queryFeeRulesHistory(req));
    }

    @Override
    public RpcResponse<FeeRulesHistoryDTO> saveFeeRulesHistory(FeeRulesHistoryRpcReq req) {
        return RpcResponse.ok(feeRulesHistoryService.saveFeeRulesHistory(req));
    }
}
