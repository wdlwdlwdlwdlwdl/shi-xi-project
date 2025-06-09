package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryNoPageRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFacade;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonService;
import io.swagger.annotations.ApiOperation;
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
public class CancelReasonFacadeImpl implements CancelReasonFacade {

    @Autowired
    private CancelReasonService cancelReasonService;

    @Autowired
    private CancelReasonConverter cancelReasonConverter;

    @Override
    public RpcResponse<PageInfo<CancelReasonDTO>> queryCancelReason(CancelReasonQueryRpcReq req) {
        return RpcResponse.ok(cancelReasonService.queryCancelReason(req));
    }

    @Override
    public RpcResponse<List<CancelReasonDTO>> queryCancelReasonAll(CancelReasonQueryNoPageRpcReq req) {
        return RpcResponse.ok(cancelReasonService.queryCancelReasonAll(req));
    }

    @Override
    public RpcResponse<CancelReasonDTO> saveCancelReason(CancelReasonRpcReq req) {
        CancelReasonDTO dto = cancelReasonService.saveCancelReason(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CancelReasonDTO> updateCancelReason(CancelReasonRpcReq req) {
        CancelReasonDTO dto = cancelReasonService.updateCancelReason(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CancelReasonDTO> cancelReasonDetail(CancelReasonRpcReq req) {
        return RpcResponse.ok(cancelReasonService.cancelReasonDetail(req));
    }

    @ApiOperation("查看取消原因")
    @Override
    public RpcResponse<CancelReasonDTO> cancelReasonDetailByCode(CancelReasonRpcReq req) {
        return RpcResponse.ok(cancelReasonService.cancelReasonDetailByCode(req));
    }

}
