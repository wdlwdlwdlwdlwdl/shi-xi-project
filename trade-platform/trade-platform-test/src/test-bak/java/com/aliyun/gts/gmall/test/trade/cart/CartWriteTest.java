package com.aliyun.gts.gmall.test.platform.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.test.platform.base.BaseCartTest;
import com.aliyun.gts.gmall.test.platform.base.TestConstants;
import org.junit.Test;
import org.testng.Assert;

import java.util.Arrays;

public class CartWriteTest extends BaseCartTest {
    private static final Long CUST_ID = TestConstants.CUST_ID;
    private static final Long ITEM_ID = TestConstants.ITEM_ID;
    private static final Long SKU_ID_1 = TestConstants.SKU_ID_1;
    private static final Long SKU_ID_2 = TestConstants.SKU_ID_2;

    @Test
    public void t001_添加购物车() {
        AddCartRpcReq req = new AddCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);
        req.setItemId(ITEM_ID);
        req.setSkuId(SKU_ID_1);
        req.setItemQty(2);

        // 加购之前
        CartItem before = getCartItem(req);
        printPretty("before", before);
        // 加购
        RpcResponse resp = cartWriteFacade.addCart(req);
        printPretty("resp", resp);
        // 加购之后
        CartItem after = getCartItem(req);
        printPretty("after", after);

        Assert.assertTrue(resp.isSuccess());
        Assert.assertNotNull(after);
        long beforeQty = before == null ? 0 : before.getQuantity();
        long addQty = after.getQuantity() - beforeQty;
        Assert.assertEquals(addQty, req.getItemQty().intValue());
    }

    @Test
    public void t002_重复添加购物车() {
        t001_添加购物车();
        t001_添加购物车();
    }

    @Test
    public void t003_修改购物车数量() {
        // 先加购一个
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);

        ModifyCartRpcReq req = new ModifyCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);
        req.setItemId(ITEM_ID);
        req.setSkuId(SKU_ID_1);
        req.setNewItemQty(17);
        RpcResponse resp = cartWriteFacade.modifyCart(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        // 查出来验证
        CartItem cartItem = getCartItem(req, req.getSkuId());
        printPretty(cartItem);
        Assert.assertNotNull(cartItem);
        Assert.assertEquals(cartItem.getQuantity().intValue(), req.getNewItemQty().intValue());
    }

    @Test
    public void t004_修改购物车SKU_合并() {
        // 先加购一个
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        ModifyCartRpcReq req = new ModifyCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);
        req.setItemId(ITEM_ID);
        req.setSkuId(SKU_ID_1);
        req.setNewSkuId(SKU_ID_2);

        // before
        CartItem beforeOld = getCartItem(req, req.getSkuId());
        CartItem beforeNew = getCartItem(req, req.getNewSkuId());
        printPretty("beforeOld", beforeOld);
        printPretty("beforeNew", beforeNew);
        Assert.assertNotNull(beforeOld);

        // modify
        RpcResponse resp = cartWriteFacade.modifyCart(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        // after
        CartItem afterOld = getCartItem(req, req.getSkuId());
        CartItem afterNew = getCartItem(req, req.getNewSkuId());
        printPretty("afterOld", afterOld);
        printPretty("afterNew", afterNew);
        Assert.assertNull(afterOld);
        Assert.assertNotNull(afterNew);
        int beforeTotal = beforeOld.getQuantity() + (beforeNew == null ? 0 : beforeNew.getQuantity());
        Assert.assertEquals(afterNew.getQuantity().intValue(), beforeTotal);
    }

    @Test
    public void t005_删除购物车() {
        // 先加购一个
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);

        DeleteCartRpcReq req = new DeleteCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);
        req.setItemSkuIds(Arrays.asList(
                new ItemSkuId(ITEM_ID, SKU_ID_1)
        ));

        // before
        CartItem before = getCartItem(req, 0);
        printPretty("before", before);
        Assert.assertNotNull(before);

        // delete
        RpcResponse resp = cartWriteFacade.deleteCart(req);
        printPretty("resp", resp);
        Assert.assertTrue(resp.isSuccess());

        // after
        CartItem after = getCartItem(req, 0);
        printPretty("after", after);
        Assert.assertNull(after);
    }

    @Test
    public void t006_删除购物车_批量() {
        // 加购2个
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        DeleteCartRpcReq req = new DeleteCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);
        req.setItemSkuIds(Arrays.asList(
                new ItemSkuId(ITEM_ID, SKU_ID_1),
                new ItemSkuId(ITEM_ID, SKU_ID_2)
        ));

        // before
        CartItem before1 = getCartItem(req, 0);
        CartItem before2 = getCartItem(req, 1);
        printPretty("before1", before1);
        printPretty("before2", before2);
        Assert.assertNotNull(before1);
        Assert.assertNotNull(before2);

        // delete
        RpcResponse resp = cartWriteFacade.deleteCart(req);
        printPretty("resp", resp);
        Assert.assertTrue(resp.isSuccess());

        // after
        CartItem after1 = getCartItem(req, 0);
        CartItem after2 = getCartItem(req, 1);
        printPretty("after1", after1);
        printPretty("after2", after2);
        Assert.assertNull(after1);
        Assert.assertNull(after2);
    }

    @Test
    public void t007_清空购物车() {
        // 加购2个
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        ClearCartRpcReq req = new ClearCartRpcReq();
        req.setClientInfo(getClientInfo());
        req.setCustId(CUST_ID);

        // before
        CartItem before1 = getCartItem(req, ITEM_ID, SKU_ID_1);
        CartItem before2 = getCartItem(req, ITEM_ID, SKU_ID_2);
        printPretty("before1", before1);
        printPretty("before2", before2);
        Assert.assertNotNull(before1);
        Assert.assertNotNull(before2);

        // clear
        RpcResponse resp = cartWriteFacade.clearCart(req);
        printPretty("resp", resp);
        Assert.assertTrue(resp.isSuccess());

        // after
        CartItem after1 = getCartItem(req, ITEM_ID, SKU_ID_1);
        CartItem after2 = getCartItem(req, ITEM_ID, SKU_ID_2);
        printPretty("after1", after1);
        printPretty("after2", after2);
        Assert.assertNull(after1);
        Assert.assertNull(after2);
    }
}
