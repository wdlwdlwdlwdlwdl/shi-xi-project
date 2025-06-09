package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFeeHistoryFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说明：取消原因实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class CancelReasonFeeHistoryFacadeImpl implements CancelReasonFeeHistoryFacade {

    @Autowired
    private CancelReasonFeeHistoryService cancelReasonFeeHistoryService;

    @Override
    public RpcResponse<List<CancelReasonFeeHistoryDTO>> queryCancelReasonFee(CancelReasonFeeHistoryRpcReq req) {
        return RpcResponse.ok(cancelReasonFeeHistoryService.queryCancelReasonFeeHistory(req));
    }

    @Override
    public RpcResponse<CancelReasonFeeHistoryDTO> saveCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req) {
        return RpcResponse.ok(cancelReasonFeeHistoryService.saveCancelReasonFeeHistory(req));
    }
}
