package com.aliyun.gts.gmall.test.trade.evoucher;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.EvoucherStatusEnum;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherQueryService;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalAgreeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalSubOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import com.aliyun.gts.gmall.test.trade.base.message.MockPayMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class EvoucherReversalTest extends EvoucherOrderCreateTest {

    @Autowired
    private ReversalWriteFacade reversalWriteFacade;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private EvoucherQueryService evoucherQueryService;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private MockPayMessage mockPayMessage;

    private static int SLEEP = 3000;

    @Test
    public void t001_电子凭证退码() throws Exception {
        MainOrder mainOrder = createEvOrder();
        long primaryId = mainOrder.getPrimaryOrderId();

        // 子订单1
        {
            long subId = mainOrder.getSubOrders().get(0).getOrderId();

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 2, 0, 1);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.ORDER_SENDED, OrderStatusEnum.ORDER_SENDED);

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 1, 0, 2);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.ORDER_SENDED, OrderStatusEnum.ORDER_SENDED);

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 0, 0, 3);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.ORDER_SENDED, OrderStatusEnum.REVERSAL_SUCCESS);
        }

        // 子订单2
        {
            long subId = mainOrder.getSubOrders().get(1).getOrderId();

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 2, 0, 1);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.ORDER_SENDED, OrderStatusEnum.ORDER_SENDED);

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 1, 0, 2);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.ORDER_SENDED, OrderStatusEnum.ORDER_SENDED);

            // 退掉一个码
            returnOneCode(primaryId, subId);
            checkEvCodeStatus(subId, 0, 0, 3);
            checkOrderStatus(primaryId, subId, OrderStatusEnum.REVERSAL_SUCCESS, OrderStatusEnum.REVERSAL_SUCCESS);
        }

    }

    private void returnOneCode(Long primaryOrderId, Long subOrderId) throws InterruptedException {
        long reversalId;
        {
            ReversalSubOrderInfo sub = new ReversalSubOrderInfo();
            sub.setOrderId(subOrderId);
            sub.setCancelAmt(1L);
            sub.setCancelQty(1);

            CreateReversalRpcReq req = new CreateReversalRpcReq();
            req.setCustId(TestConstants.CUST_ID);
            req.setPrimaryOrderId(primaryOrderId);
            req.setSubOrders(Arrays.asList(sub));
            req.setReversalChannel(OrderChannelEnum.H5.getCode());
            req.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
            req.setItemReceived(false);
            req.setReversalReasonCode(201);

            RpcResponse<Long> resp = reversalWriteFacade.createReversal(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            reversalId = resp.getData();
        }

        // 卖家同意
        {
            ReversalAgreeRpcReq req = new ReversalAgreeRpcReq();
            req.setPrimaryReversalId(reversalId);
            req.setSellerId(TestConstants.SELLER_ID);

            RpcResponse resp = reversalWriteFacade.agreeBySeller(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }

        // 等待退款完成
        Thread.sleep(SLEEP);
        mockPayMessage.sendRefundSuccess(reversalId, null);
        Thread.sleep(SLEEP);

        TcOrderDO order = tcOrderRepository.querySubByOrderId(primaryOrderId, subOrderId);
        Assert.assertNotNull(order);
        Assert.assertNotEquals(order.getOrderStatus(), OrderStatusEnum.REVERSAL_DOING.getCode());
    }

    private void checkEvCodeStatus(Long subOrderId, int notUsed, int writeOff, int disabled) {
        EvoucherQueryRpcReq req = new EvoucherQueryRpcReq();
        req.setOrderId(subOrderId);
        List<EvoucherInstance> list = evoucherQueryService.queryEvouchers(req);
        int sumNotUsed = 0;
        int sumWriteOff = 0;
        int sumDisabled = 0;
        for (EvoucherInstance ev : list) {
            EvoucherStatusEnum status = EvoucherStatusEnum.codeOf(ev.getStatus());
            if (status == EvoucherStatusEnum.NOT_USED) {
                sumNotUsed++;
            } else if (status == EvoucherStatusEnum.WRITE_OFF) {
                sumWriteOff++;
            } else if (status == EvoucherStatusEnum.DISABLED) {
                sumDisabled++;
            }
        }
        Assert.assertEquals(notUsed, sumNotUsed);
        Assert.assertEquals(writeOff, sumWriteOff);
        Assert.assertEquals(disabled, sumDisabled);
    }

    private void checkOrderStatus(Long primaryOrderId, Long subOrderId,
                                  OrderStatusEnum primaryStatus, OrderStatusEnum subStatus) {
        TcOrderDO primaryOrder = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
        Assert.assertNotNull(primaryOrder);
        Assert.assertEquals(primaryOrder.getPrimaryOrderStatus(), primaryStatus.getCode());
        Assert.assertEquals(primaryOrder.getOrderStatus(), primaryStatus.getCode());

        TcOrderDO subOrder = tcOrderRepository.querySubByOrderId(primaryOrderId, subOrderId);
        Assert.assertNotNull(subOrder);
        Assert.assertEquals(subOrder.getPrimaryOrderStatus(), primaryStatus.getCode());
        Assert.assertEquals(subOrder.getOrderStatus(), subStatus.getCode());
    }
}
