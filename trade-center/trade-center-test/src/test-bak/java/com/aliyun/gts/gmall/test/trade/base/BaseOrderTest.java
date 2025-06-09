package com.aliyun.gts.gmall.test.trade.base;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherCreateService;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherTemplate;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.test.trade.base.message.MockPayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

;

public abstract class BaseOrderTest extends BaseTest {
    private static int SLEEP = 3000;
    private static final String PAY_CH = PayChannelEnum.POINT_ASSERTS.getCode();

    @Autowired
    protected OrderReadFacade orderReadFacade;
    @Autowired
    protected OrderWriteFacade orderWriteFacade;
    @Autowired
    protected OrderQueryAbility orderQueryAbility;
    @Autowired
    protected OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    private EvoucherCreateService evoucherCreateService;
    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private MockPayMessage mockPayMessage;


    protected ConfirmItemInfo getConfirmItem(Long itemId, Long skuId, Integer qty) {
        ConfirmItemInfo item = new ConfirmItemInfo();
        item.setItemId(itemId);
        item.setSkuId(skuId);
        item.setItemQty(qty);
        return item;
    }


    protected long createOrder() {
        final Long CUST_ID = TestConstants.CUST_ID;
        final Long ITEM_ID = TestConstants.ITEM_ID;
        final Long SKU_ID_1 = TestConstants.SKU_ID_1;
        final Long SKU_ID_2 = TestConstants.SKU_ID_2;

        // 确认订单
        String token;
        {
            ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
            req.setCustId(CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setOrderItems(Arrays.asList(
                    getConfirmItem(ITEM_ID, SKU_ID_1, 3),
                    getConfirmItem(ITEM_ID, SKU_ID_2, 3)));

            RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            token = resp.getData().getConfirmOrderToken();
        }

        // 下单
        {
            CreateOrderRpcReq req = new CreateOrderRpcReq();
            req.setClientInfo(getClientInfo());
            req.setConfirmOrderToken(token);
            req.setCustId(CUST_ID);
            req.setPayChannel(PayChannelEnum.ALIPAY.getCode());
            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            return resp.getData().getOrders().get(0).getPrimaryOrderId();
        }
    }

    protected PromotionOptionDTO newOption(String optId, long sellerId, boolean coupon, boolean selected) {
        PromotionOptionDTO pro = new PromotionOptionDTO();
        pro.setOptionId(optId);
        pro.setSellerId(sellerId);
        pro.setSelected(selected);
        pro.setIsCoupon(coupon);
        return pro;
    }

    protected MainOrder getMainOrder(Long primaryOrderId) {
        return orderQueryAbility.getMainOrder(primaryOrderId,
                OrderQueryOption.builder().build());
    }

    protected MainOrder createEvOrder() throws Exception {
        // 确认下单
        String token;
        long totalFee;
        long realFee;
        long pointFee;
        {
            ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setUsePointCount(Long.MAX_VALUE);
            req.setOrderItems(Arrays.asList(
                    getConfirmItem(TestConstants.EV_ITEM_ID, TestConstants.EV_SKU_ID_1, 3),
                    getConfirmItem(TestConstants.EV_ITEM_ID, TestConstants.EV_SKU_ID_2, 3)));

            RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            token = resp.getData().getConfirmOrderToken();
            totalFee = resp.getData().getTotalAmt();
            realFee = resp.getData().getRealAmt();
            pointFee = resp.getData().getUsePointCount();
        }

        // 下单
        long primaryOrderId;
        {
            CreateOrderRpcReq req = new CreateOrderRpcReq();
            req.setClientInfo(getClientInfo());
            req.setConfirmOrderToken(token);
            req.setCustId(TestConstants.CUST_ID);
            req.setPayChannel(PAY_CH);
            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            primaryOrderId = resp.getData().getOrders().get(0).getPrimaryOrderId();
            MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            printPretty(mainOrder);
            Assert.assertNotNull(mainOrder);
            Assert.assertEquals(mainOrder.getBizCodes().size(), 1);
            Assert.assertEquals(mainOrder.getBizCodes().get(0), ExtBizCode.EVOUCHER);
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                EvoucherTemplate template = evoucherCreateService.getFromEvFeature(subOrder);
                Assert.assertNotNull(template);
            }
        }

        // 支付
        {
            OrderPayRpcReq req = new OrderPayRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setPayChannel(PAY_CH);
            req.setTotalOrderFee(totalFee);
            req.setRealPayFee(realFee);
            req.setPointCount(pointFee);
            RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            mockPayMessage.sendPaySuccess(primaryOrderId, TestConstants.CUST_ID, null);
        }

        // 等待自动发货
        MainOrder mainOrder;
        {
            Thread.sleep(SLEEP);
            mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            printPretty(mainOrder);
            Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_SENDED.getCode());
            Assert.assertNotNull(mainOrder.getOrderAttr().getSendTime());
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                List<TcEvoucherDO> list = tcEvoucherRepository.queryByOrderId(subOrder.getOrderId());
                printPretty(list);
                Assert.assertNotNull(list);
                Assert.assertEquals(3, list.size());
            }
        }
        return mainOrder;
    }
}
