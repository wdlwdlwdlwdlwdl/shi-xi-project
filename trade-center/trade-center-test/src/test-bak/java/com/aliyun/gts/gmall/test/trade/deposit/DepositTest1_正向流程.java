package com.aliyun.gts.gmall.test.trade.deposit;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.test.trade.base.BaseStepTest;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import com.aliyun.gts.gmall.test.trade.base.message.BaseTestConsumer;
import com.aliyun.gts.gmall.test.trade.base.message.TestOrderStatusChangeConsumer;
import com.aliyun.gts.gmall.test.trade.base.message.TestOrderSuccessConsumer;
import com.aliyun.gts.gmall.test.trade.base.message.TestPaySuccessConsumer;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DepositTest1_正向流程 extends BaseStepTest {

    private void test_正向流程(long pointAmt) {
        // 商品 1元, 运费 90元, 定金比例 80% (0.8元), 平台佣金 10%

        // 1阶段 80 (现金+积分)
        long pointCount = pointAmt * 20;    // 1积分 = 1000原子积分 = 0.5元
        long realAmt = 80 - pointAmt;
        long realPlatform = (long) (realAmt * 0.1);
        long realSeller = realAmt - realPlatform;
        long pointPlatform = (long) (pointAmt * 0.1);
        long pointSeller = pointAmt - pointPlatform;

        // 2阶段 原9020 (商品 20 + 运费 9000)
        // 改价1 100 (商品 20 + 运费 80)
        // 改价2 200 (商品 50 + 运费 150)
        long realAmt2 = 9020;
        long[] tailFee1 = {20, 80};
        long[] tailFee2 = {50, 150};
        long tailFeeTotal1 = 100;
        long tailFeeTotal2 = 200;
        long[] adjFee1 = {20-20, 80-9000};
        long[] adjFee2 = {50-20, 150-9000};
        long realPlatform2 = (long) (tailFeeTotal2 * 0.1);
        long realSeller2 = tailFeeTotal2 - realPlatform2;

        // 开始

        // 创建订单
        BaseTestConsumer.clearAll();
        long primaryOrderId = createOrder(TestConstants.DEP_ITEM_ID, TestConstants.DEP_SKU_ID, pointCount);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.ORDER_WAIT_PAY,
                StepOrderStatusEnum.STEP_WAIT_PAY,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(realAmt2)
                        .pointAmt(0)
                        .build());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.ORDER_WAIT_PAY);

        // 支付
        BaseTestConsumer.clearAll();
        String pay1 = payOrder(primaryOrderId);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(realAmt2)
                        .pointAmt(0)
                        .build());
        TestPaySuccessConsumer.waitMessage(primaryOrderId, pay1);
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, 1, StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE);

        // 卖家处理
        BaseTestConsumer.clearAll();
        Map<String, String> form = new HashMap<>();
        form.put("ITEM_TAIL_FEE", String.valueOf(1.0 * tailFee1[0] / 100));
        form.put("FREIGHT_FEE", String.valueOf(1.0 * tailFee1[1] / 100));
        form.put("LAST_SEND_TIME", "2030-01-01 00:00:00");
        sellerHandle(primaryOrderId, form, false);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_WAIT_CONFIRM,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal1)
                        .pointAmt(0)
                        .adjustRealAmt(tailFeeTotal1 - realAmt2)
                        .adjustPromotionAmt(adjFee1[0])
                        .adjustFreightAmt(adjFee1[1])
                        .build());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, 1, StepOrderStatusEnum.STEP_WAIT_CONFIRM);

        // 卖家改价
        BaseTestConsumer.clearAll();
        form.put("ITEM_TAIL_FEE", String.valueOf(1.0 * tailFee2[0] / 100));
        form.put("FREIGHT_FEE", String.valueOf(1.0 * tailFee2[1] / 100));
        form.put("LAST_SEND_TIME", "2030-01-01 00:00:00");
        sellerHandle(primaryOrderId, form, true);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_WAIT_CONFIRM,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal2)
                        .pointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
        TestOrderStatusChangeConsumer.waitChangePriceMessage(primaryOrderId,
                1, StepOrderStatusEnum.STEP_WAIT_CONFIRM);

        // 用户确认报价
        BaseTestConsumer.clearAll();
        customerConfirm(primaryOrderId);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_WAIT_PAY);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal2)
                        .pointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, 2, StepOrderStatusEnum.STEP_WAIT_PAY);

        // 支付尾款
        BaseTestConsumer.clearAll();
        String pay2 = payOrder(primaryOrderId);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.ORDER_WAIT_DELIVERY,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal2)
                        .pointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
        TestPaySuccessConsumer.waitMessage(primaryOrderId, pay2);
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.ORDER_WAIT_DELIVERY);

        // 卖家发货
        BaseTestConsumer.clearAll();
        sendDelivery(primaryOrderId);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.ORDER_SENDED,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal2)
                        .pointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.ORDER_SENDED);

        // 确认收货 (类目佣金为 10%)
        BaseTestConsumer.clearAll();
        confirmReceive(primaryOrderId);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.ORDER_CONFIRM,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .confirmSellerRealAmt(realSeller)
                        .confirmSellerPointAmt(pointSeller)
                        .confirmPlatformRealAmt(realPlatform)
                        .confirmPlatformPointAmt(pointPlatform)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal2)
                        .pointAmt(0)
                        .confirmSellerRealAmt(realSeller2)
                        .confirmSellerPointAmt(0)
                        .confirmPlatformRealAmt(realPlatform2)
                        .confirmPlatformPointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.ORDER_CONFIRM);
        TestOrderSuccessConsumer.waitMessage(primaryOrderId, OrderStatusEnum.ORDER_CONFIRM);
    }

    @Test
    public void test_001_纯现金() {
        test_正向流程(0);
    }

    @Test
    public void test_002_纯积分() {
        test_正向流程(80);
    }

    @Test
    public void test_003_混合() {
        test_正向流程(50);
    }

}
