package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.log.sdk.OpLogCommonUtil;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.AppIdConstants;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.BizTypeConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ClearCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.DeleteCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify.CartModifyResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartClear;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartDelete;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartWriteFacadeImpl implements CartWriteFacade {

    @Autowired
    private WorkflowEngine workflowEngine;
    @Autowired
    private WorkflowProperties workflowProperties;

    /**
     * 添加购物车接口
     * @param req
     * @return
     */
    @Override
    public RpcResponse addCart(AddCartRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartAdd add = new TCartAdd();
            add.setReq(req);
            add.setDomain(new CartItem());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartAdd(), add.toWorkflowContext());
            RpcResponse resp = add.getResponse();
            //添加购物车埋点
            return resp;
        }, "CartWriteFacadeImpl.addCart", BizCodeEntity.buildByReq(req));
    }

    /**
     * 修改购物车接口
     * @param req
     * @return
     */
    @Override
    public RpcResponse<CartModifyResultDTO> modifyCart(ModifyCartRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartModify modify = new TCartModify();
            modify.setReq(req);
            modify.setDomain(new CartItem());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartModify(), modify.toWorkflowContext());
            RpcResponse<CartModifyResultDTO> resp = modify.getResponse();
            return resp;
        }, "CartWriteFacadeImpl.modifyCart", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse deleteCart(DeleteCartRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartDelete del = new TCartDelete();
            del.setReq(req);
            del.setDomain(null);
            workflowEngine.invokeAndGetResult(workflowProperties.getCartDelete(), del.toWorkflowContext());
            return del.getResponse();
        }, "CartWriteFacadeImpl.deleteCart", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse clearCart(ClearCartRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartClear clear = new TCartClear();
            clear.setReq(req);
            clear.setDomain(null);
            workflowEngine.invokeAndGetResult(workflowProperties.getCartClear(), clear.toWorkflowContext());
            return clear.getResponse();
        }, "CartWriteFacadeImpl.clearCart", BizCodeEntity.buildByReq(req));
    }
}
