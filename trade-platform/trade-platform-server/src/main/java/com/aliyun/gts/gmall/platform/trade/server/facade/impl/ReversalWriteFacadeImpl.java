package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.log.sdk.OpLogCommonUtil;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.AppIdConstants;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.BizTypeConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractRpcRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCreate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ReversalWriteFacadeImpl implements ReversalWriteFacade {

    @Autowired
    private WorkflowEngine workflowEngine;
    @Autowired
    private ReversalProcessAbility reversalProcessAbility;
    @Autowired
    private WorkflowProperties workflowProperties;
    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Override
    public RpcResponse<Long> createReversal(CreateReversalRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TReversalCreate reversal = new TReversalCreate();
            reversal.setReq(req);
            reversal.setDomain(new MainReversal());
            workflowEngine.invokeAndGetResult(workflowProperties.getReversalCreate(), reversal.toWorkflowContext());
            RpcResponse resp = reversal.getResponse();
            //创建售后单埋点
            OpLogCommonUtil.addWithoutDiff(
                resp.isSuccess(),
                req.getClientInfo(),
                AppIdConstants.TRADE_CENTER,
                BizTypeConstants.CREATE_REVERSAL,
                req.getCustId().toString(),
                req.getCustId().toString()
            );
            // 写流水
            addFlow(req);
            return reversal.getResponse();
        }, "ReversalReadFacadeImpl.createReversal");
    }

    /**
     * 写流水
     * 写到到处都是。。。。 统一份就够了
     * @param req
     */
    private void addFlow(CreateReversalRpcReq req) {
        TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
        flow.setPrimaryOrderId(req.getPrimaryOrderId());
        flow.setOrderId(req.getPrimaryOrderId());
        flow.setFromOrderStatus(ReversalStatusEnum.COMPLETED.getCode());
        flow.setToOrderStatus(ReversalStatusEnum.WAITING_FOR_ACCEPT.getCode());
        flow.setOperatorType(0);
        flow.setGmtCreate(new Date());
        flow.setOpName(OrderStatusEnum.codeOf(ReversalStatusEnum.WAITING_FOR_ACCEPT.getCode()).getInner());
        flow.setOperator(String.valueOf(req.getCustId()));
        flow.setGmtModified(new Date());
        tcOrderOperateFlowRepository.create(flow);
    }

    @Override
    public RpcResponse agreeBySeller(ReversalAgreeRpcReq req) {
        // 对主订单加锁，当所退完情况最后一个售后单有特殊逻辑
        return callVoid(req, () -> reversalProcessAbility.sellerAgree(req), "agreeBySeller");
    }

    @Override
    public RpcResponse refuseBySeller(ReversalRefuseRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.sellerRefuse(req), "refuseBySeller");
    }

    @Override
    public RpcResponse cancelByCustomer(ReversalModifyRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.customerCancel(req), "cancelByCustomer");
    }

    @Override
    public RpcResponse sendDeliverByCustomer(ReversalDeliverRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.customerSendDeliver(req), "sendDeliverByCustomer");
    }

    @Override
    public RpcResponse confirmDeliverBySeller(ReversalModifyRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.sellerConfirmDeliver(req), "confirmDeliverBySeller");
    }

    @Override
    public RpcResponse refuseDeliverBySeller(ReversalRefuseRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.sellerRefuseDeliver(req), "refuseDeliverBySeller");
    }

    @Override
    public RpcResponse buyerConfirmRefund(ReversalBuyerConfirmReq req) {
        return callVoid(req, () -> reversalProcessAbility.buyerConfirmRefund(req), "buyerConfirmRefund");
    }

    @Override
    public RpcResponse agreeByOperation(ReversalAgreeRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.agreeByOperation(req), "agreeByOperation");
    }

    @Override
    public RpcResponse cancelBySystem(ReversalModifyRpcReq req) {
        return callVoid(req, () -> reversalProcessAbility.cancelBySystem(req), "cancelBySystem");
    }

    private RpcResponse callVoid(AbstractRpcRequest req, Runnable s, String name) {
        return RpcServerUtils.invoke(() -> {
            s.run();
            return RpcResponse.ok(null);
        }, name);
    }
}
