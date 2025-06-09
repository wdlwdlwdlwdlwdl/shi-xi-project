package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonHistoryFacade;
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
public class CancelReasonHistoryFacadeImpl implements CancelReasonHistoryFacade {

    @Autowired
    private CancelReasonHistoryService cancelReasonHistoryService;

    @Override
    public RpcResponse<List<CancelReasonHistoryDTO>> queryCancelReason(CancelReasonHistoryRpcReq req) {
        return RpcResponse.ok(cancelReasonHistoryService.queryCancelReasonHistory(req));
    }

    @Override
    public RpcResponse<CancelReasonHistoryDTO> saveCancelReasonHistory(CancelReasonHistoryRpcReq req) {
        return RpcResponse.ok(cancelReasonHistoryService.saveCancelReasonHistory(req));
    }
}
