package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFeeFacade;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonFeeService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明：取消原因费用实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class CancelReasonFeeFacadeImpl implements CancelReasonFeeFacade {

    @Autowired
    private CancelReasonFeeService cancelReasonFeeService;

    @Override
    public RpcResponse<PageInfo<CancelReasonFeeDTO>> queryCancelReasonFee(CancelReasonFeeQueryRpcReq req) {
        return RpcResponse.ok(cancelReasonFeeService.queryCancelReasonFee(req));
    }

    @Override
    public RpcResponse<CancelReasonFeeDTO> saveCancelReasonFee(CancelReasonFeeRpcReq req) {
        if(cancelReasonFeeService.exist(req)){
            throw new GmallException(CommonResponseCode.AlreadyExists);
        }
        CancelReasonFeeDTO dto = cancelReasonFeeService.saveCancelReasonFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CancelReasonFeeDTO> updateCancelReasonFee(CancelReasonFeeRpcReq req) {
        CancelReasonFeeDTO dto = cancelReasonFeeService.updateCancelReasonFee(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<CancelReasonFeeDTO> cancelReasonFeeDetail(CancelReasonFeeRpcReq req) {
        return RpcResponse.ok(cancelReasonFeeService.cancelReasonFeeDetail(req));
    }
}
