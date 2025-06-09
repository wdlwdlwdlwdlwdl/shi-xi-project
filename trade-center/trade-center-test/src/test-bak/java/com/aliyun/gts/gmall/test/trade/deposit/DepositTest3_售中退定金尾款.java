package com.aliyun.gts.gmall.test.trade.deposit;

import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderRefundDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.RefundStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.test.trade.base.BaseStepTest;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import com.aliyun.gts.gmall.test.trade.base.message.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepositTest3_售中退定金尾款 extends BaseStepTest {

    private void test_售中退定金尾款(long pointAmt) {
        // 商品 1元, 运费 90元, 定金比例 80% (0.8元), 平台佣金 10%
        long totalAmt1 = 80;
        long pointCount = pointAmt * 20;    // 1积分 = 1000原子积分 = 0.5元
        long realAmt1 = 80 - pointAmt;

        // 2阶段
        long tailFeeTotal = 200;
        long[] tailFee = {50, 150};
        long totalAmt = totalAmt1 + tailFeeTotal;
        long realAmt2 = 9020;

        // 创建订单
        long primaryOrderId = createOrder(TestConstants.DEP_ITEM_ID, TestConstants.DEP_SKU_ID, pointCount);

        // 支付
        payOrder(primaryOrderId);

        // 报尾款
        Map<String, String> form = new HashMap<>();
        form.put("ITEM_TAIL_FEE", String.valueOf(1.0 * tailFee[0] / 100));
        form.put("FREIGHT_FEE", String.valueOf(1.0 * tailFee[1] / 100));
        form.put("LAST_SEND_TIME", "2030-01-01 00:00:00");
        sellerHandle(primaryOrderId, form, false);

        // 买家确认
        customerConfirm(primaryOrderId);

        // 尾款支付
        payOrder(primaryOrderId);

        // 卖家发货
        sendDelivery(primaryOrderId);

        // 退 10% (即28分, 全定金, 28分按现金、积分分摊)
        BaseTestConsumer.clearAll();
        checkRemainFee(primaryOrderId, totalAmt, 1);
        long re1 = createRefundOnly(primaryOrderId, totalAmt / 10);
        List<Long> div1 = DivideUtils.divide(totalAmt / 10, Arrays.asList(pointAmt, realAmt1));
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkReversalFee(re1,
                CheckFee.builder()
                        .realAmt(div1.get(1))
                        .pointAmt(div1.get(0))
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re1,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);

        // 卖家同意 (剩余 252分)
        BaseTestConsumer.clearAll();
        reSellerAgree(re1);
        waitAsync(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.ORDER_SENDED,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkRemainFee(primaryOrderId, totalAmt * 9 / 10, 1);
        checkRefundStatus(re1, ReversalStatusEnum.REVERSAL_OK,
                RefundStatusEnum.REFUND_SUCCESS,
                pointAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                realAmt1 > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                null, null, null);
        TestReversalStatusChangeConsumer.waitMessage(re1,
                ReversalStatusEnum.WAIT_REFUND, ReversalStatusEnum.WAIT_SELLER_AGREE);
        TestReversalStatusChangeConsumer.waitMessage(re1,
                ReversalStatusEnum.REVERSAL_OK, ReversalStatusEnum.WAIT_REFUND);
        checkReversalFee(re1,
                CheckFee.builder()
                        .realAmt(div1.get(1))
                        .pointAmt(div1.get(0))
                        .build(),
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                true);

        MainReversal mr1 = getReversal(re1);
        OrderRefundDTO refund1 = getRefund(mr1, 1);
        TestRefundSuccessConsumer.waitMessage(refund1.getRefundId());

        // 退 70% (即196分, 定金全退(80-28), 尾款144)
        BaseTestConsumer.clearAll();
        long re2 = createRefundOnly(primaryOrderId, totalAmt * 7 / 10);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkReversalFee(re2,
                CheckFee.builder()
                        .realAmt(realAmt1 - div1.get(1))
                        .pointAmt(pointAmt - div1.get(0))
                        .build(),
                CheckFee.builder()
                        .realAmt(144)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);

        // 卖家同意
        BaseTestConsumer.clearAll();
        reSellerAgree(re2);
        waitAsync(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.ORDER_SENDED,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkRemainFee(primaryOrderId, totalAmt * 2 / 10, 1);
        checkRefundStatus(re2, ReversalStatusEnum.REVERSAL_OK,
                RefundStatusEnum.REFUND_SUCCESS,
                pointAmt > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                realAmt1 > 0 ? RefundStatusEnum.REFUND_SUCCESS : null,
                RefundStatusEnum.REFUND_SUCCESS, null, RefundStatusEnum.REFUND_SUCCESS);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.WAIT_REFUND, ReversalStatusEnum.WAIT_SELLER_AGREE);
        TestReversalStatusChangeConsumer.waitMessage(re2,
                ReversalStatusEnum.REVERSAL_OK, ReversalStatusEnum.WAIT_REFUND);
        checkReversalFee(re2,
                CheckFee.builder()
                        .realAmt(realAmt1 - div1.get(1))
                        .pointAmt(pointAmt - div1.get(0))
                        .build(),
                CheckFee.builder()
                        .realAmt(144)
                        .pointAmt(0)
                        .build(),
                true);

        MainReversal mr2 = getReversal(re2);
        OrderRefundDTO refund21 = getRefund(mr2, 1);
        OrderRefundDTO refund22 = getRefund(mr2, 2);
        TestRefundSuccessConsumer.waitMessage(refund21.getRefundId());
        TestRefundSuccessConsumer.waitMessage(refund22.getRefundId());

        // 退最后 20% (全部尾款)
        BaseTestConsumer.clearAll();
        long re3 = createRefundOnly(primaryOrderId, totalAmt * 2 / 10);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.REVERSAL_DOING,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkReversalFee(re3,
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                CheckFee.builder()
                        .realAmt(totalAmt * 2 / 10)
                        .pointAmt(0)
                        .build(),
                false);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.WAIT_SELLER_AGREE, null);

        // 卖家同意 (退完)
        BaseTestConsumer.clearAll();
        reSellerAgree(re3);
        waitAsync(primaryOrderId, OrderStatusEnum.REVERSAL_DOING);
        checkStatus(primaryOrderId, 2,
                OrderStatusEnum.REVERSAL_SUCCESS,
                StepOrderStatusEnum.STEP_FINISH,
                StepOrderStatusEnum.STEP_FINISH);
        checkRemainFee(primaryOrderId, 0, 1);
        checkRefundStatus(re3, ReversalStatusEnum.REVERSAL_OK,
                null, null, null,
                RefundStatusEnum.REFUND_SUCCESS, null, RefundStatusEnum.REFUND_SUCCESS);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.WAIT_REFUND, ReversalStatusEnum.WAIT_SELLER_AGREE);
        TestReversalStatusChangeConsumer.waitMessage(re3,
                ReversalStatusEnum.REVERSAL_OK, ReversalStatusEnum.WAIT_REFUND);
        checkReversalFee(re3,
                CheckFee.builder()
                        .realAmt(0)
                        .pointAmt(0)
                        .build(),
                CheckFee.builder()
                        .realAmt(totalAmt * 2 / 10)
                        .pointAmt(0)
                        .build(),
                true);

        MainReversal mr3 = getReversal(re3);
        OrderRefundDTO refund31 = getRefund(mr3, 1);
        OrderRefundDTO refund32 = getRefund(mr3, 2);
        Assert.assertNull(refund31);
        Assert.assertNotNull(refund32);
        TestRefundSuccessConsumer.waitMessage(refund32.getRefundId());
        TestOrderStatusChangeConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_SUCCESS);
        TestOrderSuccessConsumer.waitMessage(primaryOrderId, OrderStatusEnum.REVERSAL_SUCCESS);

        long tailFeeTotal2 = 200;
        long[] adjFee2 = {50 - 20, 150 - 9000};
        checkStepFee(primaryOrderId,
                CheckFee.builder()
                        .realAmt(realAmt1)
                        .pointAmt(pointAmt)
                        .confirmSellerRealAmt(0)
                        .confirmSellerPointAmt(0)
                        .confirmPlatformRealAmt(0)
                        .confirmPlatformPointAmt(0)
                        .build(),
                CheckFee.builder()
                        .realAmt(tailFeeTotal)
                        .pointAmt(0)
                        .confirmSellerRealAmt(0)
                        .confirmSellerPointAmt(0)
                        .confirmPlatformRealAmt(0)
                        .confirmPlatformPointAmt(0)
                        .adjustRealAmt(tailFeeTotal2 - realAmt2)
                        .adjustPromotionAmt(adjFee2[0])
                        .adjustFreightAmt(adjFee2[1])
                        .build());
    }

    @Test
    public void test_001_售中退定金尾款_现金() {
        test_售中退定金尾款(0);
    }

    @Test
    public void test_002_售中退定金尾款_积分() {
        test_售中退定金尾款(80);
    }

    @Test
    public void test_003_售中退定金尾款_混合() {
        test_售中退定金尾款(50);
    }
}
