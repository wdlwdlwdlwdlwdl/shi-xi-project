package com.aliyun.gts.gmall.test.platform.base;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

public abstract class BaseCartTest extends BaseTest {

    @Autowired
    protected CartReadFacade cartReadFacade;

    @Autowired
    protected CartWriteFacade cartWriteFacade;

    @Autowired
    protected CartService cartService;

    protected CartItem getCartItem(AddCartRpcReq req) {
        CartItemUk uk = new CartItemUk(
                req.getCustId(),
                req.getCartType(),
                new ItemSkuId(req.getItemId(), req.getSkuId()));
        return cartService.queryItem(uk);
    }

    protected CartItem getCartItem(ModifyCartRpcReq req, Long skuId) {
        CartItemUk uk = new CartItemUk(
                req.getCustId(),
                req.getCartType(),
                new ItemSkuId(req.getItemId(), skuId));
        return cartService.queryItem(uk);
    }

    protected CartItem getCartItem(DeleteCartRpcReq req, int index) {
        CartItemUk uk = new CartItemUk(
                req.getCustId(),
                req.getCartType(),
                new ItemSkuId(
                        req.getItemSkuIds().get(index).getItemId(),
                        req.getItemSkuIds().get(index).getSkuId()));
        return cartService.queryItem(uk);
    }

    protected CartItem getCartItem(ClearCartRpcReq req, long itemId, long skuId) {
        CartItemUk uk = new CartItemUk(
                req.getCustId(),
                req.getCartType(),
                new ItemSkuId(itemId, skuId));
        return cartService.queryItem(uk);
    }

    protected void addCart(Long custId, Long itemId, Long skuId) {
        AddCartRpcReq req = new AddCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(custId);
        req.setItemId(itemId);
        req.setSkuId(skuId);
        req.setItemQty(2);
        RpcResponse resp = cartWriteFacade.addCart(req);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void clearCart(Long custId) {
        ClearCartRpcReq req = new ClearCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(custId);
        RpcResponse resp = cartWriteFacade.clearCart(req);
        Assert.assertTrue(resp.isSuccess());
    }

    protected ItemSkuQty newItemSkuQty(long itemId, long skuId, Integer qty) {
        ItemSkuQty r = new ItemSkuQty();
        r.setItemId(itemId);
        r.setSkuId(skuId);
        r.setQty(qty);
        return r;
    }

}
