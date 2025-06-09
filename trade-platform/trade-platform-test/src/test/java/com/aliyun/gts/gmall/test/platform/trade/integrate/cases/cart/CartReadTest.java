package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.cart;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CalCartPriceRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CheckAddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.QueryCartItemQuantityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.CartPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.SellerPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseCartTest;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.TestConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class CartReadTest extends BaseCartTest {
    private static final Long CUST_ID = TestConstants.CUST_ID;
    private static final Long ITEM_ID = TestConstants.ITEM_ID;
    private static final Long SKU_ID_1 = TestConstants.SKU_ID_1;
    private static final Long SKU_ID_2 = TestConstants.SKU_ID_2;

    @Test
    public void t001_检查加购() {
        CheckAddCartRpcReq req = new CheckAddCartRpcReq();
        req.setCustId(CUST_ID);
        req.setItemId(ITEM_ID);
        req.setSkuId(SKU_ID_1);
        RpcResponse resp = cartReadFacade.checkAddCart(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }

    @Test
    public void t002_购物车数量查询() {
        // 加购2个
        clearCart(CUST_ID);
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        QueryCartItemQuantityRpcReq req = new QueryCartItemQuantityRpcReq();
        req.setCustId(CUST_ID);
        RpcResponse<Integer> resp = cartReadFacade.queryCartItemQuantity(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertEquals(resp.getData().intValue(), 2);
    }

    @Test
    public void t003_购物车列表查询() {
        // 加购2个
        clearCart(CUST_ID);
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        CartQueryRpcReq req = new CartQueryRpcReq();
        req.setCustId(CUST_ID);
        req.setPage(new PageParam(1, 10));
        req.setChannel(OrderChannelEnum.H5.getCode());
        RpcResponse<CartDTO> resp = cartReadFacade.queryCart(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        CartDTO cart = resp.getData();
        Assert.assertNotNull(cart);
        Assert.assertEquals(cart.getTotalItemCount().intValue(), 2);
        Assert.assertNotNull(cart.getGroups());
        Assert.assertEquals(cart.getGroups().size(), 1);

        CartGroupDTO group = cart.getGroups().get(0);
        Assert.assertNotNull(group);
        Assert.assertNotNull(group.getItems());
        Assert.assertNotNull(group.getGroupType());
        Assert.assertNotNull(group.getSellerId());
        Assert.assertNotNull(group.getSellerName());
        Assert.assertEquals(group.getItems().size(), 2);

        for (CartItemDTO item : group.getItems()) {
            Assert.assertNotNull(item);
            Assert.assertEquals(item.getCartQty().intValue(), 2);
            Assert.assertEquals(item.getItemId(), ITEM_ID);
            Assert.assertTrue(SKU_ID_1.equals(item.getSkuId()) || SKU_ID_2.equals(item.getSkuId()));
            Assert.assertTrue(item.getItemPrice() > 0);
            Assert.assertTrue(item.getOriginPrice() > 0);
            Assert.assertNotNull(item.getDesc());
            Assert.assertNotNull(item.getSkuPic());
        }
    }

    @Test
    public void t004_购物车价格计算() {
        // 加购2个
        clearCart(CUST_ID);
        addCart(CUST_ID, ITEM_ID, SKU_ID_1);
        addCart(CUST_ID, ITEM_ID, SKU_ID_2);

        CalCartPriceRpcReq req = new CalCartPriceRpcReq();
        req.setCustId(CUST_ID);
        req.setChannel(OrderChannelEnum.H5.getCode());
        req.setItemSkuIds(Arrays.asList(
                newItemSkuQty(ITEM_ID, SKU_ID_1, null),
                newItemSkuQty(ITEM_ID, SKU_ID_2, null)
        ));
        RpcResponse<CartPriceDTO> resp = cartReadFacade.calculateCartPrice(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        CartPriceDTO price = resp.getData();
        Assert.assertNotNull(price);
        Assert.assertTrue(price.getPromotionPrice() > 0);
        //Assert.assertTrue(price.getOptions().size() > 0);
        Assert.assertTrue(price.getSellers().size() > 0);

        for (PromotionOptionDTO pro : price.getOptions()) {
            //Assert.assertTrue(pro.getReduceFee() > 0);
        }
        long totalAmt = 0L;
        for (SellerPriceDTO slr : price.getSellers()) {
            Assert.assertTrue(slr.getPromotionPrice() > 0);
            Assert.assertTrue(slr.getItems().size() > 0);
            //Assert.assertTrue(slr.getOptions().size() > 0);
            long slrAmt = 0L;
            for (ItemPriceDTO item : slr.getItems()) {
                Assert.assertTrue(item.getItemPrice() > 0);
                Assert.assertTrue(item.getPromotionPrice() > 0);
                Assert.assertTrue(item.getItemId() > 0);
                Assert.assertTrue(item.getSkuId() > 0);
                Assert.assertTrue(item.getSkuQty() > 0);
                slrAmt += item.getPromotionPrice();
                totalAmt += item.getPromotionPrice();
            }
            Assert.assertEquals(slr.getPromotionPrice().longValue(), slrAmt);
            for (PromotionOptionDTO p : slr.getOptions()) {
                Assert.assertTrue(p.getReduceFee() > 0);
            }
        }
        Assert.assertEquals(price.getPromotionPrice().longValue(), totalAmt);
    }
}
