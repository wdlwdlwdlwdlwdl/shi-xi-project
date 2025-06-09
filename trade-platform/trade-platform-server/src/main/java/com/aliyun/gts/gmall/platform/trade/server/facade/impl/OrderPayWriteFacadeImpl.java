package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.log.sdk.OpLogCommonUtil;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.AppIdConstants;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.BizTypeConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderMergePayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayV2RpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发起支付请求实现类
 *
 * @author xinchen
 */
@Service
@Slf4j
public class OrderPayWriteFacadeImpl implements OrderPayWriteFacade {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Override
    public RpcResponse<OrderPayRpcResp> toPay(OrderPayRpcReq orderPayRpcReq) {
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", orderPayRpcReq);
        OrderPayRpcResp result = null;
        FailInfo fail = null;
        try {
            result = (OrderPayRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getToPay(),
                toPayContext
            );
        } catch (GmallException e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(e.getFrontendCare());
        } catch (Exception e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR);
        }
        //发起支付行为日志
        OpLogCommonUtil.update(
            fail == null,
            orderPayRpcReq.getClientInfo(),
            AppIdConstants.TRADE_CENTER, BizTypeConstants.TO_PAY,
            orderPayRpcReq.getPrimaryOrderId().toString()
        );
        if (fail != null) {
            return RpcResponse.fail(fail);
        }
        return RpcResponse.ok(result);
    }


    @Override
    public RpcResponse<OrderMergePayRpcResp> toMergePay(OrderMergePayRpcReq orderMergePayRpcReq) {
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", orderMergePayRpcReq);
        OrderMergePayRpcResp result = null;
        FailInfo fail = null;
        try {
            result = (OrderMergePayRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getToMergePay(),
                toPayContext
            );
        } catch (GmallException e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(e.getFrontendCare());
        } catch (Exception e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR);
        }
        //发起支付行为日志
        for (Long primaryOrderId : orderMergePayRpcReq.getPrimaryOrderIds()) {
            OpLogCommonUtil.update(
                fail == null,
                orderMergePayRpcReq.getClientInfo(),
                AppIdConstants.TRADE_CENTER,
                "TO_MERGE_PAY",
                primaryOrderId.toString()
            );
        }
        if (fail != null) {
            return RpcResponse.fail(fail);
        }
        return RpcResponse.ok(result);
    }

    /**
     * 发起支付处理
     * @param orderPayV2RpcReq
     * @param orderPayRpcResp
     * @return
     */
    public  RpcResponse<Boolean> paymentV2(OrderPayV2RpcReq orderPayV2RpcReq, OrderPayRpcResp orderPayRpcResp) {
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setAccountId(orderPayV2RpcReq.getAccountId());
        ePayTokenRpcReq.setInvoiceID(String.valueOf(orderPayRpcResp.getCartId()));
        ePayTokenRpcReq.setAmount(orderPayV2RpcReq.getRealPayFee());
        ePayTokenRpcReq.setCardId(orderPayV2RpcReq.getCardId());
        ePayTokenRpcReq.setScope("payment");
        ePayTokenRpcReq.setDescription(orderPayV2RpcReq.getDescription());
        ePayTokenRpcReq.setEmail(orderPayV2RpcReq.getEmail());
        ePayTokenRpcReq.setPhone(orderPayV2RpcReq.getPhone());
        ePayTokenRpcReq.setLanguage(orderPayV2RpcReq.getLang());
        ePayTokenRpcReq.setPaymentType("cardId");
        ePayTokenRpcReq.setCustId(orderPayV2RpcReq.getCustId().toString());
        ePayTokenRpcReq.setToken(orderPayRpcResp.getPayToken());
        return orderPayRepository.paymentV2(ePayTokenRpcReq, orderPayRpcResp);
    }

    /**
     * 取消订单，具体实现在center：com.aliyun.gts.gmall.center.trade.server.ext.toPay.OrderPayWriteFacadeImplExt#cancelOrders
     * @param primaryOrderIds
     * @return
     */
    public RpcResponse<Boolean> cancelOrders(List<Long> primaryOrderIds) {
        return RpcResponse.ok(true);
    }
}
