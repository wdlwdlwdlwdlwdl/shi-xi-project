package com.aliyun.gts.gmall.test.platform.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.PayRenderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.test.platform.base.BaseOrderTest;
import com.aliyun.gts.gmall.test.platform.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OrderPayTest extends BaseOrderTest {

    private static final Long CUST_ID = TestConstants.CUST_ID;
    private static final String PAY_CH = "103";
    private static final String PAY_ALIPAY_CHANNLE = "101";

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    private OrderPayReadFacade orderPayReadFacade;

    @Test
    public void to_pay_test() {
        Long primaryOrderId = createOrder();
        toPay(primaryOrderId);
    }

    @Test
    public void to_pay_render() {
        Long primaryOrderId = createOrder();

        PayRenderRpcReq req = new PayRenderRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel("h5");
        req.setPrimaryOrderId(primaryOrderId);
        RpcResponse<PayRenderRpcResp> result = orderPayReadFacade.payRender(req);
        printPretty(result);
        Assert.assertTrue(result.isSuccess());
    }

    @Test
    public void confirm_pay_check() {
        Long primaryOrderId = createOrder();
        OrderPayRpcResp pay = toPay(primaryOrderId);

        ConfirmPayCheckRpcReq req = new ConfirmPayCheckRpcReq();
        req.setCustId(CUST_ID);
        req.setPrimaryOrderId(primaryOrderId);
        req.setPayFlowId(pay.getPayFlowId());
        RpcResponse<ConfirmPayCheckRpcResp> resp = orderPayReadFacade.confirmPay(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
    }
}
