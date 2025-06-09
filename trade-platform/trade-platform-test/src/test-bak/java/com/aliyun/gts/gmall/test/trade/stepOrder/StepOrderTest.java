package com.aliyun.gts.gmall.test.platform.stepOrder;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.test.platform.base.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class StepOrderTest extends BaseStepTest {

    @Autowired
    protected OrderReadFacade orderReadFacade;
    @Autowired
    protected OrderWriteFacade orderWriteFacade;
    @Autowired
    protected OrderQueryAbility orderQueryAbility;
    @Autowired
    protected OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    protected OrderPayReadFacade orderPayReadFacade;
    @Autowired
    protected TcOrderRepository tcOrderRepository;

    protected long createOrder() {
        return createOrder(TestConstants.ITEM_ID, TestConstants.SKU_ID_1, 0);
    }

    @Test
    public void test_001_下单() {
        long primaryOrderId = createOrder();

        OrderDetailQueryRpcReq req = new OrderDetailQueryRpcReq();
        req.setPrimaryOrderId(primaryOrderId);
        req.setCustId(TestConstants.CUST_ID);
        RpcResponse<MainOrderDetailDTO> resp = orderReadFacade.queryOrderDetail(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        MainOrderDetailDTO detail = resp.getData();
        Assert.assertNotNull(detail.getStepOrders());
        Assert.assertEquals(detail.getStepOrders().size(), 3);
    }

    @Test
    public void test_002_正向() {
        // 创单
        long primaryOrderId = createOrder();

        // 阶段1 - 支付
        payOrder(primaryOrderId, 1);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_PAY,
                StepOrderStatusEnum.STEP_WAIT_START);

        // 阶段2 - 支付
        payOrder(primaryOrderId, 2);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);

        // 阶段2 - 卖家操作
        Map<String, String> formData1 = new HashMap<>();
        formData1.put("TAIL_PRICE", "10000");
        sellerHandle(primaryOrderId, formData1);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_CONFIRM,
                StepOrderStatusEnum.STEP_WAIT_START);

        // 阶段2 - 用户确认
        customerConfirm(primaryOrderId);
        checkStatus(primaryOrderId, 3,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_PAY);

        // 阶段3 - 支付
        payOrder(primaryOrderId, 3);
        checkStatus(primaryOrderId, 3,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE);

        // 阶段3 - 卖家操作
        Map<String, String> formData2 = new HashMap<>();
        formData2.put("LOGISTICS_COMPANY", "1");
        formData2.put("LOGISTICS_CODE", "123");
        sellerHandle(primaryOrderId, formData2);
        checkStatus(primaryOrderId, 3,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_CONFIRM);

        // 阶段3 - 用户确认
        customerConfirm(primaryOrderId);
        checkStatus(primaryOrderId, 3,
                OrderStatusEnum.ORDER_CONFIRM,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
    }
}
