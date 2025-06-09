package com.aliyun.gts.gmall.test.platform.base;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.PayRenderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.test.platform.mock.MockPayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;

public abstract class BaseOrderTest extends BaseTest {

    @Autowired
    protected OrderReadFacade orderReadFacade;

    @Autowired
    protected OrderWriteFacade orderWriteFacade;

    @Autowired
    protected OrderQueryAbility orderQueryAbility;

    @Autowired
    private MockPayMessage mockPayMessage;

    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;

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
            req.setPayChannel("101");
            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            return resp.getData().getOrders().get(0).getPrimaryOrderId();
        }
    }

    protected OrderPayRpcResp toPay(Long primaryOrderId) {
        return toPay(primaryOrderId, true);
    }

    protected OrderPayRpcResp toPay(Long primaryOrderId, boolean waitSuccess) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        OrderPayRpcReq req = new OrderPayRpcReq();
        req.setOrderChannel(OrderChannelEnum.H5.getCode());
        req.setPayChannel("101");
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setBuyerName(mainOrder.getCustomer().getCustName());

        RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        // 支付成功消息, 由paycenter发送, 本地测试收不到, 这里mock发送一下
        mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        mockPayMessage.sendPaySuccess(mainOrder, null);

        if (waitSuccess) {
            sleep(5000);
            mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            Assert.assertEquals(OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode(), mainOrder.getPrimaryOrderStatus());
        }
        return resp.getData();
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
                OrderQueryOption.builder()
                        .includeExtends(true).build());
    }
}
