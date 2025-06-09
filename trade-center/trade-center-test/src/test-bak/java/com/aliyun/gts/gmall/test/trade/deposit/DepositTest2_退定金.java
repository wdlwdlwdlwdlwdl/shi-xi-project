package com.aliyun.gts.gmall.test.trade.deposit;

import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderRefundDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.RefundStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.test.trade.base.BaseStepTest;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import com.aliyun.gts.gmall.test.trade.base.message.*;
import org.junit.Assert;
import org.junit.Test;

public class DepositTest2_退定金 extends BaseStepTest {

    private void test_退定金(long pointAmt) {
        // 商品 1元, 运费 90元, 定金比例 80% (0.8元), 平台佣金 10%
        long totalAmt = 80;
        long pointCount = pointAmt * 20;    // 1积分 = 1000原子积分 = 0.5元
        long realAmt = 80 - pointAmt;

        long realAmt2 = 9020;


        // 创建订单
        long primaryOrderId = createOrder(TestConstants.DEP_ITEM_ID, TestConstants.DEP_SKU_ID, pointCount);

        // 支付
        payOrder(primaryOrderId);

        // 退款 10%
        BaseTestConsumer.clearAll();
        checkRemainFee(primaryOrderId, totalAmt, 1);
        long re1 = createRefundOnly(primaryOrderId, totalAmt / 10);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkReversalFee(re1,
                CheckFee.builder()
                        .realAmt(realAmt / 10)
                        .pointAmt(pointAmt / 10)
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re1,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);
        // 没发订单状态消息
        //TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);

        // 拒绝退款
        BaseTestConsumer.clearAll();
        reSellerRefuse(re1);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkRemainFee(primaryOrderId, totalAmt, 1);
        checkRefundStatus(re1, ReversalStatusEnum.SELLER_REFUSE,
                null, null, null,
                null, null, null);
        TestReversalStatusChangeConsumer.waitMessage(re1,
                ReversalStatusEnum.SELLER_REFUSE, ReversalStatusEnum.WAIT_SELLER_AGREE);
        // 没发订单状态消息
        //TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.STEP_ORDER_DOING);

        // 再退款
        BaseTestConsumer.clearAll();
        long re2 = createRefundOnly(primaryOrderId, totalAmt / 10);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkReversalFee(re2,
                CheckFee.builder()
                        .realAmt(realAmt / 10)
                        .pointAmt(pointAmt / 10)
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);
        // 没发订单状态消息
        //TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);

        // 同意退款
        BaseTestConsumer.clearAll();
        reSellerAgree(re2);
        waitAsync(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.STEP_ORDER_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkRemainFee(primaryOrderId, totalAmt * 9 / 10, 1);
        checkRefundStatus(re2, ReversalStatusEnum.REVERSAL_OK,
                RefundStatusEnum.REFUND_SUCCESS,
                pointAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                realAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                null, null, null);
        checkReversalFee(re2,
                CheckFee.builder()
                        .realAmt(realAmt / 10)
                        .pointAmt(pointAmt / 10)
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                true);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.WAIT_REFUND, ReversalStatusEnum.WAIT_SELLER_AGREE);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.REVERSAL_OK, ReversalStatusEnum.WAIT_REFUND);

        MainReversal mr2 = getReversal(re2);
        OrderRefundDTO refund2 = getRefund(mr2, 1);
        TestRefundSuccessConsumer.waitMessage(refund2.getRefundId());
        // 没发订单状态消息
        //TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.STEP_ORDER_DOING);

        // 全部退完
        BaseTestConsumer.clearAll();
        long re3 = createRefundOnly(primaryOrderId, totalAmt * 9 / 10);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkReversalFee(re3,
                CheckFee.builder()
                        .realAmt(realAmt * 9 / 10)
                        .pointAmt(pointAmt * 9 / 10)
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);
        // 没发订单状态消息
        //TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);

        // 同意退款
        BaseTestConsumer.clearAll();
        reSellerAgree(re3);
        waitAsync(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);
        checkStatus(primaryOrderId, 1,
                OrderStatusEnum.REVERSAL_SUCCESS,
                StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE,
                StepOrderStatusEnum.STEP_WAIT_START);
        checkRemainFee(primaryOrderId, 0, 1);
        checkRefundStatus(re3, ReversalStatusEnum.REVERSAL_OK,
                RefundStatusEnum.REFUND_SUCCESS,
                pointAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                realAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                null, null, null);
        checkReversalFee(re3,
                CheckFee.builder()
                        .realAmt(realAmt * 9 / 10)
                        .pointAmt(pointAmt * 9 / 10)
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                true);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.WAIT_REFUND, ReversalStatusEnum.WAIT_SELLER_AGREE);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.REVERSAL_OK, ReversalStatusEnum.WAIT_REFUND);

        MainReversal mr3 = getReversal(re3);
        OrderRefundDTO refund3 = getRefund(mr3, 1);
        TestRefundSuccessConsumer.waitMessage(refund3.getRefundId());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_SUCCESS);
        TestOrderSuccessConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_SUCCESS);

        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt)
                        .pointAmt(pointAmt)
                        .confirmSellerRealAmt(0)
                        .confirmSellerPointAmt(0)
                        .confirmPlatformRealAmt(0)
                        .confirmPlatformPointAmt(0)
                        .build(),
                CheckFee.builder()
                        .realAmt(realAmt2)
                        .pointAmt(0)
                        .confirmSellerRealAmt(0)
                        .confirmSellerPointAmt(0)
                        .confirmPlatformRealAmt(0)
                        .confirmPlatformPointAmt(0)
                        .build());
    }

    @Test
    public void test_001_退定金_现金() {
        test_退定金(0);
    }

    @Test
    public void test_002_退定金_积分() {
        test_退定金(80);
    }

    @Test
    public void test_003_退定金_混合() {
        test_退定金(30);
    }
}
