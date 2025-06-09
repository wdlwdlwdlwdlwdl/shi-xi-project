package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.ConfirmOrderSplitReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.OrderExtFacade;
import com.aliyun.gts.gmall.center.trade.core.domainservice.MixOrderSplitService;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderExtFacadeImpl implements OrderExtFacade {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Value("${trade.flow.ext.orderCheckSplit:flow/ext/order/orderCheckSplit}")
    private String flowOrderCheckSplit;

    @Autowired
    private MixOrderSplitService mixOrderSplitService;

    @Override
    public RpcResponse<ConfirmOrderSplitDTO> checkSplit(ConfirmOrderSplitReq req) {
        return RpcServerUtils.invoke(() -> {
            CreatingOrder ord = new CreatingOrder();
            mixOrderSplitService.setCheckSplit(ord);
            TOrderConfirm ctx = new TOrderConfirm();
            ctx.setReq(req);
            ctx.setDomain(ord);
            workflowEngine.invokeAndGetResult(flowOrderCheckSplit, ctx.toWorkflowContext());
            RpcResponse resp = ctx.getResponse();
            if (!resp.isSuccess()) {
                return RpcResponse.fail(resp.getFail());
            }

            ConfirmOrderSplitDTO result = mixOrderSplitService.getSplitResult(ord);
            return RpcResponse.ok(result);
        }, "OrderExtFacadeImpl.checkSplit", BizCodeEntity.buildByReq(req));
    }
}
