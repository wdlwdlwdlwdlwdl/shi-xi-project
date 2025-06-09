package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartPayQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartReadFacadeImpl implements CartReadFacade {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Override
    public RpcResponse<CartDTO> queryCart(CartQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartQuery t = new TCartQuery();
            t.setReq(req);
            t.setDomain(new Cart());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartQuery(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.queryCart", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse<CartItemDTO> querySingleItem(CartSingleQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartSingleQuery t = new TCartSingleQuery();
            t.setReq(req);
            t.setDomain(new Cart());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartSingleQuery(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.querySingleItem", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse<CartDTO> calculateCartPrice(CalCartPriceRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartCalc t = new TCartCalc();
            t.setReq(req);
            t.setDomain(new Cart());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartCalcPrice(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.calculateCartPrice", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse checkAddCart(CheckAddCartRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartAdd t = new TCartAdd();
            t.setReq(req);
            t.setDomain(new CartItem());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartCheckAdd(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.checkAddCart", BizCodeEntity.buildByReq(req));
    }

    @Override
    public RpcResponse<Integer> queryCartItemQuantity(QueryCartItemQuantityRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartCount t = new TCartCount();
            t.setReq(req);
            t.setDomain(null);
            workflowEngine.invokeAndGetResult(workflowProperties.getCartQueryCount(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.queryCartItemQuantity", BizCodeEntity.buildByReq(req));
    }

    @Override
    @ApiOperation("购物车商品数量V2查询接口,商品数量")
    public RpcResponse<Integer> queryCartItemV2Quantity(QueryCartItemQuantityRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartCount t = new TCartCount();
            t.setReq(req);
            t.setDomain(null);
            workflowEngine.invokeAndGetResult(workflowProperties.getCartItemV2QueryCount(), t.toWorkflowContext());
            return t.getResponse();
        }, "CartReadFacadeImpl.queryCartItemQuantity", BizCodeEntity.buildByReq(req));
    }

    /**
     * 购物车查看接口--根据支付方式
     * @param req
     * @return
     */
    @ApiOperation("购物车查看接口--根据支付方式")
    public RpcResponse<CartDTO> queryCarPayMode(CartPayQueryRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TCartPayQuery tCartPayQuery = new TCartPayQuery();
            tCartPayQuery.setReq(req);
            tCartPayQuery.setDomain(new Cart());
            workflowEngine.invokeAndGetResult(workflowProperties.getCartQueryPayMode(), tCartPayQuery.toWorkflowContext());
            return tCartPayQuery.getResponse();
        }, "CartReadFacadeImpl.queryCarPayMode", BizCodeEntity.buildByReq(req));
    }
}
