package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.reversal;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseReversalTest;
import com.aliyun.gts.gmall.test.platform.trade.integrate.mock.MockPayMessage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class ReversalAdvanceTest extends BaseReversalTest {
    private static int SLEEP = 1; // not sleep

    @Autowired
    private MockPayMessage mockPayMessage;

    @Test
    public void r01_售中3退2_拒绝() throws Exception {
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_SENDED;

        List<Long> order = createOrd(orderFromStatus, 100, 3);
        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createRefundOnly(r1);
        Long reversal2 = createRefundOnly(r2);
        checkReversal(reversal1, OrderStageEnum.ON_SALE);
        checkReversal(reversal2, OrderStageEnum.ON_SALE);

        // 拒绝第1笔
        {
            p01_sellerRefuse(reversal1);

            // 售后单状态，拒绝
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.SELLER_REFUSE.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：回滚到已发货
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(1), OrderStageEnum.ON_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
            // 子单3：已发货
            assertOrderStatus(list.get(3), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(3), OrderStageEnum.ON_SALE);
        }

        // 拒绝第2笔
        {
            p01_sellerRefuse(reversal2);

            // 售后单状态，拒绝
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.SELLER_REFUSE.getCode().intValue());

            // 订单状态, 回归到已发货
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());
            for (int i = 0; i < list.size(); i++) {
                assertOrderStatus(list.get(i), orderFromStatus, orderFromStatus);
                assertOrderStage(list.get(i), OrderStageEnum.ON_SALE);
            }
        }
    }

    @Test
    public void r02_售中多笔_3退2_部分金额_同意() throws Exception {
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_SENDED;

        List<Long> order = createOrd(orderFromStatus, 100, 3);
        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createRefundOnly(r1);
        Long reversal2 = createRefundOnly(r2);
        checkReversal(reversal1, OrderStageEnum.ON_SALE);
        checkReversal(reversal2, OrderStageEnum.ON_SALE);

        // 同意第1笔
        {
            p01_sellerAgree(reversal1);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal1, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：回退
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(1), OrderStageEnum.ON_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
            // 子单3：已发货
            assertOrderStatus(list.get(3), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(3), OrderStageEnum.ON_SALE);
        }

        // 同意第2笔
        {
            p01_sellerAgree(reversal2);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal2, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：已发货
            assertOrderStatus(list.get(0), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：回退
            assertOrderStatus(list.get(1), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(1), OrderStageEnum.ON_SALE);
            // 子单2：回退
            assertOrderStatus(list.get(2), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
            // 子单3：已发货
            assertOrderStatus(list.get(3), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(3), OrderStageEnum.ON_SALE);
        }
    }

    @Test
    public void r03_售中多笔_3退2_全部金额_同意() throws Exception {
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_SENDED;

        List<Long> order = createOrd(orderFromStatus, 100, 3);
        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createRefundOnly(r1, true);
        Long reversal2 = createRefundOnly(r2, true);
        checkReversal(reversal1, OrderStageEnum.ON_SALE);
        checkReversal(reversal2, OrderStageEnum.ON_SALE);

        // 同意第1笔
        {
            p01_sellerAgree(reversal1);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal1, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：售后完成
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
            // 子单3：已发货
            assertOrderStatus(list.get(3), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(3), OrderStageEnum.ON_SALE);
        }

        // 同意第2笔
        {
            p01_sellerAgree(reversal2);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal2, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：已发货
            assertOrderStatus(list.get(0), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：售后完成
            assertOrderStatus(list.get(1), orderFromStatus, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：售后完成
            assertOrderStatus(list.get(2), orderFromStatus, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(2), OrderStageEnum.AFTER_SALE);
            // 子单3：已发货
            assertOrderStatus(list.get(3), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(3), OrderStageEnum.ON_SALE);
        }
    }

    @Test
    public void r04_售中多笔_2退2_全部金额_同意() throws Exception {
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_SENDED;

        List<Long> order = createOrd(orderFromStatus, 100, 2);
        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createRefundOnly(r1, true);
        Long reversal2 = createRefundOnly(r2, true);
        checkReversal(reversal1, OrderStageEnum.ON_SALE);
        checkReversal(reversal2, OrderStageEnum.ON_SALE);

        // 同意第1笔
        {
            p01_sellerAgree(reversal1);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal1, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：售后完成
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
        }

        // 同意第2笔
        {
            p01_sellerAgree(reversal2);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal2, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：完结
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(0), OrderStageEnum.AFTER_SALE);
            // 子单1：完结
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：完结
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(2), OrderStageEnum.AFTER_SALE);
        }
    }

    @Test
    public void r05_售中多笔_2退2_部分金额_退货_同意() throws Exception {
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_SENDED;

        List<Long> order = createOrd(orderFromStatus, 100, 2);
        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createReturnItem(r1);
        Long reversal2 = createReturnItem(r2);
        checkReversal(reversal1, OrderStageEnum.ON_SALE);
        checkReversal(reversal2, OrderStageEnum.ON_SALE);

        // 完成第1笔
        {
            p01_sellerAgree(reversal1);
            p02_customerSend(reversal1);
            p03_sellerReceive(reversal1);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal1, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.ON_SALE);
            // 子单1：回退
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.ON_SALE);
        }

        // 完成第2笔
        {
            p01_sellerAgree(reversal2);
            p02_customerSend(reversal2);
            p03_sellerReceive(reversal2);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal2, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：完结
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(0), OrderStageEnum.AFTER_SALE);
            // 子单1：完结
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：完结
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
            assertOrderStage(list.get(2), OrderStageEnum.AFTER_SALE);
        }
    }

    @Test
    public void r10_售后退款_部分金额() throws Exception {
        List<Long> order = createOrd(OrderStatusEnum.ORDER_SENDED, 100, 2);
        confirmReceiveOrder(order.get(0));
        OrderStatusEnum orderFromStatus = OrderStatusEnum.ORDER_CONFIRM;

        List<Long> r1 = Arrays.asList(order.get(0), order.get(1));
        List<Long> r2 = Arrays.asList(order.get(0), order.get(2));
        Long reversal1 = createRefundOnly(r1);
        Long reversal2 = createRefundOnly(r2);
        checkReversal(reversal1, OrderStageEnum.AFTER_SALE);
        checkReversal(reversal2, OrderStageEnum.AFTER_SALE);

        // 完成第1笔
        {
            p01_sellerAgree(reversal1);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal1, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal1, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：售后中
            assertOrderStatus(list.get(0), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(0), OrderStageEnum.AFTER_SALE);
            // 子单1：回退
            assertOrderStatus(list.get(1), OrderStatusEnum.REVERSAL_DOING, orderFromStatus);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：售后中
            assertOrderStatus(list.get(2), OrderStatusEnum.REVERSAL_DOING, OrderStatusEnum.REVERSAL_DOING);
            assertOrderStage(list.get(2), OrderStageEnum.AFTER_SALE);
        }

        // 完成第2笔
        {
            p01_sellerAgree(reversal2);
            sleep(SLEEP);
            mockPayMessage.sendRefundSuccess(reversal2, null);
            sleep(SLEEP);

            // 售后单状态，完成
            MainReversal reversal = reversalQueryService.queryReversal(reversal2, null);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());

            // 订单状态
            List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(reversal.getMainOrder().getPrimaryOrderId());

            // 主单：回退
            assertOrderStatus(list.get(0), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(0), OrderStageEnum.AFTER_SALE);
            // 子单1：回退
            assertOrderStatus(list.get(1), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(1), OrderStageEnum.AFTER_SALE);
            // 子单2：回退
            assertOrderStatus(list.get(2), orderFromStatus, orderFromStatus);
            assertOrderStage(list.get(2), OrderStageEnum.AFTER_SALE);
        }
    }

    private void checkReversal(Long reversalId, OrderStageEnum stage) {
        MainReversal mainReversal = reversalQueryService.queryReversal(reversalId, null);
        Assert.assertNotNull(mainReversal);
        Assert.assertEquals(mainReversal.getReversalFeatures().getOrderStage().intValue(),
                stage.getCode().intValue());

        {
            long sepSum = mainReversal.getReversalFeatures().getCancelSeparateRealAmt().values().stream().mapToLong(Long::longValue).sum();
            if (stage == OrderStageEnum.AFTER_SALE) {
                Assert.assertEquals(sepSum, mainReversal.getCancelAmt().longValue());
            } else {
                Assert.assertEquals(sepSum, 0);
            }
            long sum = mainReversal.getReversalFeatures().getCancelPointAmt() + mainReversal.getReversalFeatures().getCancelRealAmt();
            Assert.assertEquals(sum, mainReversal.getCancelAmt().longValue());
        }
        for (SubReversal subReversal : mainReversal.getSubReversals()) {
            long sepSum = subReversal.getReversalFeatures().getCancelSeparateRealAmt().values().stream().mapToLong(Long::longValue).sum();
            if (stage == OrderStageEnum.AFTER_SALE) {
                Assert.assertEquals(sepSum, subReversal.getCancelAmt().longValue());
            } else {
                Assert.assertEquals(sepSum, 0);
            }
            long sum = subReversal.getReversalFeatures().getCancelPointAmt() + subReversal.getReversalFeatures().getCancelRealAmt();
            Assert.assertEquals(sum, subReversal.getCancelAmt().longValue());
        }
    }
}
