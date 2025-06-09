package com.aliyun.gts.gmall.test.platform.stepOrder;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmStepOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import com.aliyun.gts.gmall.test.platform.base.TestConstants;
import com.aliyun.gts.gmall.test.platform.mock.MockPayMessage;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseStepTest extends BaseTest {


    @Autowired
    protected OrderWriteFacade orderWriteFacade;
    @Autowired
    protected OrderReadFacade orderReadFacade;
    @Autowired
    protected OrderQueryAbility orderQueryAbility;
    @Autowired
    protected OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    protected TcOrderRepository tcOrderRepository;
    @Autowired
    private MockPayMessage mockPayMessage;


    protected long createOrder(long itemId, long skuId, long usePointCount) {
        // 确认订单
        String token;
        long realAmt;
        {
            ConfirmItemInfo item = new ConfirmItemInfo();
            item.setItemId(itemId);
            item.setSkuId(skuId);
            item.setItemQty(1);

            Map<String, String> map = new HashMap<>();
            map.put("STEP_PRICE", "10,20");
            ConfirmStepOrderInfo step = new ConfirmStepOrderInfo();
            step.setStepTemplateName("test");
            step.setStepContextProps(map);

            ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setOrderItems(Arrays.asList(item));
            req.setUsePointCount(usePointCount);
            req.setConfirmStepOrderInfo(step);
            RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
            printPretty("confirmOrderInfo", resp);
            Assert.assertTrue(resp.isSuccess());
            token = resp.getData().getConfirmOrderToken();
            realAmt = resp.getData().getRealAmt();
        }

        // 创建订单
        {
            CreateOrderRpcReq req = new CreateOrderRpcReq();
            req.setClientInfo(getClientInfo());
            req.setConfirmOrderToken(token);
            req.setCustId(TestConstants.CUST_ID);
            if (realAmt == 0) {
                req.setPayChannel("103");
            } else {
                req.setPayChannel("101");
            }

            RpcResponse<CreateOrderResultDTO> resp = orderWriteFacade.createOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            return resp.getData().getOrders().get(0).getPrimaryOrderId();
        }
    }

    protected void payOrder(long primaryOrderId, Integer stepNo) {
        String payChannel;
        MainOrder mainOrder;
        {
            mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            long realAmt = mainOrder.getOrderPrice().getOrderRealAmt();
            if (realAmt > 0) {
                payChannel = "101";
            } else {
                payChannel = "103";
            }
        }
        String payFlowId;
        {
            OrderPayRpcReq req = new OrderPayRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setCustId(TestConstants.CUST_ID);
            req.setOrderChannel(OrderChannelEnum.H5.getCode());
            req.setPayChannel(payChannel);
            //req.setTotalOrderFee(realFee);
            //req.setRealPayFee(realFee);
            // req.setStepNo(stepNo);
            RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
            printPretty("toPay", resp);
            Assert.assertTrue(resp.isSuccess());
            payFlowId = resp.getData().getPayFlowId();
        }
        mockPayMessage.sendPaySuccess(mainOrder, stepNo);
        sleep(3000L);
    }

    protected void sellerHandle(long primaryOrderId, Map<String, String> formData) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        StepOrderHandleRpcReq req = new StepOrderHandleRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setSellerId(mainOrder.getSeller().getSellerId());
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setStepNo(mainOrder.getOrderAttr().getCurrentStepNo());
        req.setFormData(formData);
        RpcResponse resp = orderWriteFacade.handleStepOrderBySeller(req);
        printPretty("handleStepOrderBySeller", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void customerConfirm(long primaryOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        StepOrderHandleRpcReq req = new StepOrderHandleRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        req.setSellerId(mainOrder.getSeller().getSellerId());
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setStepNo(mainOrder.getOrderAttr().getCurrentStepNo());
        RpcResponse resp = orderWriteFacade.confirmStepOrderByCustomer(req);
        printPretty("confirmStepOrderByCustomer", resp);
        Assert.assertTrue(resp.isSuccess());
    }

    protected void checkStatus(long primaryOrderId, int stepNo, OrderStatusEnum orderStatus, StepOrderStatusEnum... stepStatus) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        Assert.assertNotNull(mainOrder);

        List<TcOrderDO> orders = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        Assert.assertEquals(2, orders.size());
        for (TcOrderDO order : orders) {
            Assert.assertEquals(orderStatus.getCode(), order.getOrderStatus());
            Assert.assertEquals(orderStatus.getCode(), order.getPrimaryOrderStatus());
        }

        Assert.assertEquals(stepNo, mainOrder.orderAttr().getCurrentStepNo().intValue());
        for (int i = 0; i < stepStatus.length; i++) {
            StepOrder step = mainOrder.getStepOrders().get(i);
            Assert.assertEquals(step.getStatus(), stepStatus[i].getCode());
        }
    }
}
