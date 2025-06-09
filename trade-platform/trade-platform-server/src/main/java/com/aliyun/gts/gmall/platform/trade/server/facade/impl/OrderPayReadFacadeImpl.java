package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmingPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.PayRenderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmingPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayWriteService;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class OrderPayReadFacadeImpl implements OrderPayReadFacade {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private PayWriteService payWriteService;

    @Override
    public RpcResponse<PayRenderRpcResp> payRender(PayRenderRpcReq payRenderRpcReq) {
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", payRenderRpcReq);
        try {
            PayRenderRpcResp resp = (PayRenderRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getPayRender(),
                toPayContext
            );
            return RpcResponse.ok(resp);
        } catch (GmallException e) {
            log.error("OrderPayReadFacadeImpl.payRender return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        } catch (Exception e) {
            log.error("OrderPayReadFacadeImpl.payRender return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR));
        }
    }

    @Override
    public RpcResponse<ConfirmPayCheckRpcResp> confirmPay(ConfirmPayCheckRpcReq confirmPayCheckRpcReq) {
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", confirmPayCheckRpcReq);
        try {
            ConfirmPayCheckRpcResp resp = (ConfirmPayCheckRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getConfirmPay(),
                toPayContext
            );
            return RpcResponse.ok(resp);
        } catch (GmallException e) {
            log.error("OrderPayReadFacadeImpl.confirmPay return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        } catch (Exception e) {
            log.error("OrderPayReadFacadeImpl.confirmPay return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR));
        }
    }


    @Override
    public RpcResponse<String> getPayTokenInfo(String token) {
        String value = payWriteService.getPayToken(token);
        return RpcResponse.ok(value);
    }

    @Override
    public RpcResponse<ConfirmingPayCheckRpcResp> confirmingPay(ConfirmingPayCheckRpcReq confirmingPayCheckRpcReq) {
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", confirmingPayCheckRpcReq);
        try {
            ConfirmingPayCheckRpcResp resp = (ConfirmingPayCheckRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getConfirmingPay(),
                toPayContext
            );
            return RpcResponse.ok(resp);
        } catch (GmallException e) {
            log.error("OrderPayReadFacadeImpl.confirmingPay return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(e.getFrontendCare()));
        } catch (Exception e) {
            log.error("OrderPayReadFacadeImpl.confirmingPay return occurred exceptions!", e);
            return RpcResponse.fail(ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR));
        }
    }

}
