package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CheckReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.GetReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalAgreeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalCheckDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseReversalTest;
import com.aliyun.gts.gmall.test.platform.trade.integrate.mock.MockPayMessage;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReversalCreateTest extends BaseReversalTest {
    private static final int WAIT_ASYNC = 1; //not sleep
    private static final OrderStatusEnum FROM_STATUS = OrderStatusEnum.ORDER_SENDED;

    @Autowired
    private MockPayMessage mockPayMessage;

    @Test
    public void r001_售后校验() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        CheckReversalRpcReq req = new CheckReversalRpcReq();
        req.setCustId(CUST_ID);
        req.setPrimaryOrderId(order.get(0));
        req.setReversalChannel(OrderChannelEnum.H5.getCode());
        RpcResponse<ReversalCheckDTO> resp = reversalReadFacade.checkReversal(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertTrue(resp.getData().getSubOrders().size() > 0);
        for (ReversalSubOrderDTO sub : resp.getData().getSubOrders()) {
            Assert.assertTrue(sub.getOrderId() > 0);
            Assert.assertTrue(sub.getMaxCancelAmt() > 0);
            Assert.assertTrue(sub.getMaxCancelQty() > 0);
        }
    }

    @Test
    public void r002_售后原因查询() {
        GetReasonRpcReq req = new GetReasonRpcReq();
        req.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        RpcResponse<List<ReversalReasonDTO>> resp = reversalReadFacade.queryReversalReasons(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertTrue(resp.getData().size() > 0);
        for (ReversalReasonDTO sub : resp.getData()) {
            Assert.assertNotNull(sub.getReasonMessage());
            Assert.assertTrue(sub.getReasonCode() > 0);
        }
    }

    @Test
    public void r010_发起售后_仅退款() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createRefundOnly(order);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertTrue(main.getCancelAmt() > 0L);
    }

    @Test
    public void r011_卖家同意_仅退款_现金积分混合() throws Exception {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createRefundOnly(order, true);

        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setSellerId(SELLER_ID);

        RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_REFUND.getCode());

        // 等待异步扣款成功
        sleep(WAIT_ASYNC);
        mockPayMessage.sendRefundSuccess(main, null);
        sleep(WAIT_ASYNC);
        main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.REVERSAL_OK.getCode());

        // 订单状态, 为售后完成
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
        }
    }

    @Test
    public void r012_卖家同意_仅退款_纯积分() throws Exception {
        List<Long> order = createOrd(FROM_STATUS, Long.MAX_VALUE);
        Long reversalId = createRefundOnly(order, true);

        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setSellerId(SELLER_ID);

        RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_REFUND.getCode());

        // 等待异步扣款成功
        sleep(WAIT_ASYNC);
        mockPayMessage.sendRefundSuccess(main, null);
        sleep(WAIT_ASYNC);
        main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.REVERSAL_OK.getCode());

        // 订单状态, 为售后完成
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
        }
    }

    @Test
    public void r013_卖家同意_仅退款_纯现金() throws Exception {
        List<Long> order = createOrd(FROM_STATUS, 0);
        Long reversalId = createRefundOnly(order, true);

        ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
        req.setPrimaryReversalId(reversalId);
        req.setSellerId(SELLER_ID);

        RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_REFUND.getCode());

        // 等待异步扣款成功
        sleep(WAIT_ASYNC);
        mockPayMessage.sendRefundSuccess(main, null);
        sleep(WAIT_ASYNC);
        main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.REVERSAL_OK.getCode());

        // 订单状态, 为售后完成
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
        }
    }

    @Test
    public void r020_发起售后_退货退款() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertTrue(main.getCancelAmt() > 0L);
    }

    @Test
    public void r021_卖家同意_退货退款() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p01_sellerAgree(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_DELIVERY.getCode());
    }

    @Test
    public void r022_顾客发货() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p01_sellerAgree(reversalId);
        p02_customerSend(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_CONFIRM_RECEIVE.getCode());
    }

    @Test
    public void r023_卖家收货() throws Exception {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p01_sellerAgree(reversalId);
        p02_customerSend(reversalId);
        p03_sellerReceive(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.WAIT_REFUND.getCode());

        // 等待异步扣款成功
        sleep(WAIT_ASYNC);
        mockPayMessage.sendRefundSuccess(main, null);
        sleep(WAIT_ASYNC);
        main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.REVERSAL_OK.getCode());

        // 订单状态, 为售后完成
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue());
        }
    }

    @Test
    public void r030_卖家收货_拒收() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p01_sellerAgree(reversalId);
        p02_customerSend(reversalId);
        p03_sellerRefuseReceive(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.SELLER_REFUSE.getCode());

        // 订单状态, 退回到待发货
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
        }
    }


    @Test
    public void r031_顾客取消() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p0x_customerCancel(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.CUSTOMER_CLOSE.getCode());

        // 订单状态, 退回到待发货
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
        }
    }

    @Test
    public void r032_顾客取消_卖家同意后的() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        Long reversalId = createReturnItem(order);
        p01_sellerAgree(reversalId);
        p0x_customerCancel(reversalId);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertEquals(main.getReversalStatus(), ReversalStatusEnum.CUSTOMER_CLOSE.getCode());

        // 订单状态, 退回到待发货
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
            Assert.assertEquals(ord.getOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
        }
    }


    @Test
    public void r041_部分退款_发起() {
        List<Long> order = createOrd(FROM_STATUS, 100);
        order.remove(2);
        Long reversalId = createRefundOnly(order);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertTrue(main.getCancelAmt() > 0L);
        Assert.assertNotNull(main.getMainOrder().getPrimaryOrderId());

        // 订单状态, 主单为售后中, 子单一个售后中一个待发货
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            if (ord.getPrimaryOrderFlag().intValue() == 1) {
                Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_DOING.getCode().intValue());
                Assert.assertEquals(ord.getOrderStatus().intValue(), OrderStatusEnum.REVERSAL_DOING.getCode().intValue());
            } else {
                OrderStatusEnum status = order.get(1).longValue() == ord.getOrderId().longValue() ?
                        OrderStatusEnum.REVERSAL_DOING : FROM_STATUS;
                Assert.assertEquals(ord.getOrderStatus().intValue(), status.getCode().intValue());
                Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), OrderStatusEnum.REVERSAL_DOING.getCode().intValue());
            }
        }
    }

    @Test
    public void r042_部分退款_同意() throws Exception {
        List<Long> order = createOrd(FROM_STATUS, 100);
        order.remove(2);
        Long reversalId = createRefundOnly(order, true);
        p01_sellerAgree(reversalId);

        // 等待异步退款完成，和状态更新
        sleep(WAIT_ASYNC);
        mockPayMessage.sendRefundSuccess(reversalId, null);
        sleep(WAIT_ASYNC);

        MainReversal main = getReversal(reversalId);
        Assert.assertNotNull(main);
        Assert.assertTrue(main.getCancelAmt() > 0L);
        Assert.assertNotNull(main.getMainOrder().getPrimaryOrderId());

        // 订单状态, 主单为待发货, 子单一个售后完成一个待发货
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(main.getMainOrder().getPrimaryOrderId());
        for (TcOrderDO ord : list) {
            if (ord.getPrimaryOrderFlag().intValue() == 1) {
                Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
                Assert.assertEquals(ord.getOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
            } else {
                OrderStatusEnum status = order.get(1).longValue() == ord.getOrderId().longValue() ?
                        OrderStatusEnum.REVERSAL_SUCCESS : FROM_STATUS;
                Assert.assertEquals(ord.getOrderStatus().intValue(), status.getCode().intValue());
                Assert.assertEquals(ord.getPrimaryOrderStatus().intValue(), FROM_STATUS.getCode().intValue());
            }
        }
    }
}
