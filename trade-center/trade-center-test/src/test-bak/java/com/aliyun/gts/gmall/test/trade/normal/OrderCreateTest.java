package com.aliyun.gts.gmall.test.trade.normal;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.PrimaryOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.test.trade.base.BaseOrderTest;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.testng.Assert;

import java.util.Arrays;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrderCreateTest extends BaseOrderTest {

    private static final Long CUST_ID = TestConstants.CUST_ID;
    private static final Long ITEM_ID = TestConstants.ITEM_ID;
    private static final Long SKU_ID_1 = TestConstants.SKU_ID_1;
    private static final Long SKU_ID_2 = TestConstants.SKU_ID_2;
    private static final String PAY_CH = PayChannelEnum.POINT_ASSERTS.getCode();
    private static final String PAY_ALIPAY = PayChannelEnum.ALIPAY.getCode();

    private RpcResponse<ConfirmOrderDTO> x011_确认订单展示() {
        ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel(OrderChannelEnum.H5.getCode());
        req.setOrderItems(Arrays.asList(
                getConfirmItem(ITEM_ID, SKU_ID_1, 3),
                getConfirmItem(ITEM_ID, SKU_ID_2, 3)));

        RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        return resp;
    }

    @Test
    public void t011_确认订单展示() {
        RpcResponse<ConfirmOrderDTO> resp = x011_确认订单展示();
        ConfirmOrderDTO dto = resp.getData();
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getRealAmt() > 0);
        Assert.assertNotNull(dto.getMaxAvailablePoint());
        Assert.assertTrue(dto.getOrderGroups().size() == 1);
        Assert.assertNotNull(dto.getReceiver());
        Assert.assertNotNull(dto.getReceiver().getDeliveryAddr());
        Assert.assertNotNull(dto.getConfirmOrderToken());

        OrderGroupDTO group = dto.getOrderGroups().get(0);
        Assert.assertTrue(group.getTotalAmt() > 0);
        Assert.assertTrue(group.getFreight() > 0);
        Assert.assertTrue(group.getRealAmt() > 0);
        Assert.assertTrue(group.getOrderItems().size() == 2);

        for (OrderItemDTO item : group.getOrderItems()) {
            Assert.assertNotNull(item.getItemId());
            Assert.assertNotNull(item.getSkuId());
            Assert.assertNotNull(item.getSkuDesc());
            Assert.assertNotNull(item.getWeight());
            Assert.assertTrue(item.getOrderQty() == 3);
            Assert.assertNotNull(item.getSkuPic());
        }
    }

    @Test
    public void t012_创建订单() {
        RpcResponse<ConfirmOrderDTO> x = x011_确认订单展示();

        CreateOrderRpcReq req = new CreateOrderRpcReq();
        req.setClientInfo(getClientInfo());
        req.setConfirmOrderToken(x.getData().getConfirmOrderToken());
        req.setCustId(CUST_ID);
        req.setPayChannel(PAY_ALIPAY);

        RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        CreateOrderResultDTO dto = resp.getData();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getOrders());
        Assert.assertTrue(dto.getOrders().size() == 1);

        PrimaryOrderResultDTO ord = dto.getOrders().get(0);
        Assert.assertNotNull(ord.getPrimaryOrderId());

        // 查询订单

        MainOrder mainOrder = getMainOrder(ord.getPrimaryOrderId());
        printPretty(mainOrder);
        Assert.assertNotNull(mainOrder);
        Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_WAIT_PAY.getCode());
        Assert.assertEquals(mainOrder.getBizCodes().size(), 1);
        Assert.assertEquals(mainOrder.getBizCodes().get(0), BizCodeEnum.NORMAL_TRADE.getCode());
        Assert.assertTrue(mainOrder.getSubOrders().size() == 2);
        Assert.assertEquals(mainOrder.getInventoryReduceType(), InventoryReduceType.REDUCE_ON_PAY.getCode());
        Assert.assertEquals(mainOrder.getOrderAttr().getLogisticsType(), LogisticsTypeEnum.REALITY.getCode());
        Assert.assertEquals(mainOrder.getOrderType().intValue(), OrderTypeEnum.PHYSICAL_GOODS.getCode().intValue());

        OrderPrice price = mainOrder.getOrderPrice();
        Assert.assertNotNull(price);
        Assert.assertTrue(nullZero(price.getOrderRealAmt()) > 0);
        Assert.assertTrue(nullZero(price.getPointAmt()) == 0);
        Assert.assertTrue(nullZero(price.getOrderPromotionAmt()) > 0);
        Assert.assertTrue(nullZero(price.getItemOriginAmt()) > 0);
        Assert.assertTrue(nullZero(price.getFreightAmt()) >= 0);

        long sumRealAmt = 0;
        long sumPointAmt = 0;
        long sumSaleAmt = 0;
        long sumFreight = 0;
        for (SubOrder sub : mainOrder.getSubOrders()) {
            Assert.assertNotNull(sub.getItemSku());
            Assert.assertNotNull(sub.getOrderPrice());
            Assert.assertTrue(sub.getOrderQty() > 0);
            Assert.assertTrue(sub.getOrderId() > 0);
            Assert.assertNotNull(sub.getItemSku().getWeight());
            Assert.assertNotNull(sub.getItemSku().getSkuPic());
            sumRealAmt += nullZero(sub.getOrderPrice().getOrderRealAmt());
            sumPointAmt += nullZero(sub.getOrderPrice().getPointAmt());
            sumSaleAmt += nullZero(sub.getOrderPrice().getItemOriginAmt());
            sumFreight += nullZero(sub.getOrderPrice().getFreightAmt());
        }

        // 价格汇总对比
        Assert.assertEquals(nullZero(price.getOrderRealAmt()), sumRealAmt);
        Assert.assertEquals(nullZero(price.getPointAmt()), sumPointAmt);
        Assert.assertEquals(nullZero(price.getItemOriginAmt()), sumSaleAmt);
        Assert.assertEquals(nullZero(price.getFreightAmt()), sumFreight);

        // 支付单
//        OrderPay orderPay = mainOrder.getOrderPay();
//        Assert.assertNotNull(orderPay);
//        Assert.assertEquals(orderPay.getPaymentStatus(), PayStatusEnum.TO_BE_PAID.getCode());
//        Assert.assertEquals(orderPay.getPayChannel(), PAY_ALIPAY);
//        Assert.assertTrue(nullZero(orderPay.getTotalOrderFee()) > 0);
//        Assert.assertEquals(nullZero(orderPay.getPayDiscountAmt()), 0L);
    }

    private RpcResponse<ConfirmOrderDTO> x021_确认订单展示_纯积分支付() {
        ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel(OrderChannelEnum.H5.getCode());
        req.setUsePointCount(Long.MAX_VALUE);
//        req.setOrderItems(Arrays.asList(
//                getConfirmItem(ITEM_ID, SKU_ID_1, 3),
//                getConfirmItem(ITEM_ID, SKU_ID_2, 3)));

        req.setOrderItems(Arrays.asList(
                getConfirmItem(5688L, 66162L, 3)));

        RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        return resp;
    }

    @Test
    public void t021_确认订单展示_纯积分支付() {
        RpcResponse<ConfirmOrderDTO> resp = x021_确认订单展示_纯积分支付();
        ConfirmOrderDTO dto = resp.getData();
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getRealAmt().intValue(), 0);
        Assert.assertTrue(dto.getUsePointCount() > 0);
        Assert.assertTrue(dto.getMaxAvailablePoint() > 0);
        Assert.assertTrue(dto.getOrderGroups().size() == 1);
        Assert.assertNotNull(dto.getReceiver());
        Assert.assertNotNull(dto.getReceiver().getDeliveryAddr());
        Assert.assertNotNull(dto.getConfirmOrderToken());

        OrderGroupDTO group = dto.getOrderGroups().get(0);
        Assert.assertTrue(group.getTotalAmt() > 0);
        Assert.assertTrue(group.getFreight() > 0);
        Assert.assertEquals(0, group.getRealAmt().intValue());
        Assert.assertEquals(1, group.getOrderItems().size());

        for (OrderItemDTO item : group.getOrderItems()) {
            Assert.assertNotNull(item.getItemId());
            Assert.assertNotNull(item.getSkuId());
            Assert.assertNotNull(item.getSkuDesc());
            Assert.assertTrue(item.getOrderQty() == 3);
            Assert.assertNotNull(item.getWeight());
            Assert.assertNotNull(item.getSkuPic());
        }
    }

    @Test
    public void t022_创建订单_纯积分支付() {
        RpcResponse<ConfirmOrderDTO> x = x021_确认订单展示_纯积分支付();
        CreateOrderRpcReq req = new CreateOrderRpcReq();
        req.setClientInfo(getClientInfo());
        req.setConfirmOrderToken(x.getData().getConfirmOrderToken());
        req.setCustId(CUST_ID);
        req.setPayChannel(PAY_CH);

        RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        CreateOrderResultDTO dto = resp.getData();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getOrders());
        Assert.assertTrue(dto.getOrders().size() == 1);

        PrimaryOrderResultDTO ord = dto.getOrders().get(0);
        Assert.assertNotNull(ord.getPrimaryOrderId());

        // 查询订单

        MainOrder mainOrder = getMainOrder(ord.getPrimaryOrderId());
        printPretty(mainOrder);
        Assert.assertNotNull(mainOrder);
        Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_WAIT_PAY.getCode());
        Assert.assertEquals(mainOrder.getBizCodes().size(), 1);
        Assert.assertEquals(mainOrder.getBizCodes().get(0), BizCodeEnum.NORMAL_TRADE.getCode());
        Assert.assertEquals(1, mainOrder.getSubOrders().size());
        Assert.assertEquals(mainOrder.getInventoryReduceType(), InventoryReduceType.REDUCE_ON_PAY.getCode());
        Assert.assertEquals(mainOrder.getOrderAttr().getLogisticsType(), LogisticsTypeEnum.REALITY.getCode());
        Assert.assertEquals(mainOrder.getOrderType().intValue(), OrderTypeEnum.PHYSICAL_GOODS.getCode().intValue());

        OrderPrice price = mainOrder.getOrderPrice();
        Assert.assertNotNull(price);
        Assert.assertEquals(nullZero(price.getOrderRealAmt()), 0L);
        Assert.assertTrue(nullZero(price.getPointAmt()) > 0);
        Assert.assertTrue(nullZero(price.getOrderPromotionAmt()) > 0);
        Assert.assertTrue(nullZero(price.getItemOriginAmt()) > 0);
        Assert.assertTrue(nullZero(price.getFreightAmt()) >= 0);

        long sumRealAmt = 0;
        long sumPointAmt = 0;
        long sumSaleAmt = 0;
        for (SubOrder sub : mainOrder.getSubOrders()) {
            Assert.assertNotNull(sub.getItemSku());
            Assert.assertNotNull(sub.getOrderPrice());
            Assert.assertNotNull(sub.getItemSku().getWeight());
            Assert.assertNotNull(sub.getItemSku().getSkuPic());
            Assert.assertTrue(sub.getOrderQty() > 0);
            Assert.assertTrue(sub.getOrderId() > 0);
            sumRealAmt += nullZero(sub.getOrderPrice().getOrderRealAmt());
            sumPointAmt += nullZero(sub.getOrderPrice().getPointAmt());
            sumSaleAmt += nullZero(sub.getOrderPrice().getItemOriginAmt());
        }

        //价格汇总对比
        Assert.assertEquals(nullZero(price.getOrderRealAmt().longValue()), sumRealAmt);
        Assert.assertEquals(nullZero(price.getPointAmt().longValue()), sumPointAmt);
        Assert.assertEquals(nullZero(price.getItemOriginAmt().longValue()), sumSaleAmt);

        // 支付单
//        OrderPay orderPay = mainOrder.getOrderPay();
//        Assert.assertNotNull(orderPay);
//        Assert.assertEquals(orderPay.getPaymentStatus(), PayStatusEnum.TO_BE_PAID.getCode());
//        Assert.assertEquals(orderPay.getPayChannel(), PAY_CH);
//        Assert.assertEquals(nullZero(orderPay.getRealPayFee()), 0L);
//        Assert.assertEquals(nullZero(orderPay.getPayDiscountAmt()), 0L);
        //return mainOrder;
    }

    @Test
    public void t100_确认订单展示_不配送地区() {
        ReceiverDTO rec = new ReceiverDTO();
        rec.setProvinceCode("99999991");
        rec.setCityCode("99999992");
        rec.setDistrictCode("99999993");

        ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel(OrderChannelEnum.H5.getCode());
        req.setOrderItems(Arrays.asList(
                getConfirmItem(ITEM_ID, SKU_ID_1, 3),
                getConfirmItem(ITEM_ID, SKU_ID_2, 3)));
        req.setReceiver(rec);

        RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertEquals(resp.getData().getNonblockFails().get(0).getCode(), OrderErrorCode.RECEIVER_NOT_SUPPORT_BY_ITEM.getCode());
        Assert.assertEquals(resp.getData().getNonblockFails().get(0).getSellerId().longValue(), 1L);
    }
}
