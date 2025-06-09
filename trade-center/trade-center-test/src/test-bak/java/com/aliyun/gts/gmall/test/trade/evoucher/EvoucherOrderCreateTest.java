package com.aliyun.gts.gmall.test.trade.evoucher;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherModifyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.EvoucherDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.EvoucherFacade;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherCreateService;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.test.trade.base.BaseOrderTest;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

import java.util.Arrays;
import java.util.List;

public class EvoucherOrderCreateTest extends BaseOrderTest {

    private static final long ITEM_ID = TestConstants.EV_ITEM_ID;
    private static final long SKU_ID_1 = TestConstants.EV_SKU_ID_1;
    private static final long SKU_ID_2 = TestConstants.EV_SKU_ID_2;
    private static final long CUST_ID = TestConstants.CUST_ID;

    private static final Long NORMAL_ITEM_ID = TestConstants.ITEM_ID;
    private static final Long NORMAL_SKU_ID = TestConstants.SKU_ID_1;

    private static final String CH = OrderChannelEnum.H5.getCode();

    @Autowired
    private EvoucherCreateService evoucherCreateService;
    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private EvoucherFacade evoucherFacade;

    @Test
    public void t001_电子凭证_合并下单报错() {
        ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel(CH);
        req.setOrderItems(Arrays.asList(
                getConfirmItem(ITEM_ID, SKU_ID_1, 3),
                getConfirmItem(ITEM_ID, SKU_ID_2, 3),
                getConfirmItem(NORMAL_ITEM_ID, NORMAL_SKU_ID, 3)));

        RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
        printPretty(resp);
        Assert.assertFalse(resp.isSuccess());
    }

    @Test
    public void t002_电子凭证_确认下单() {
        ConfirmOrderInfoRpcReq req = new ConfirmOrderInfoRpcReq();
        req.setCustId(CUST_ID);
        req.setOrderChannel(CH);
        req.setOrderItems(Arrays.asList(
                getConfirmItem(ITEM_ID, SKU_ID_1, 3),
                getConfirmItem(ITEM_ID, SKU_ID_2, 3)));

        RpcResponse<ConfirmOrderDTO> resp = orderReadFacade.confirmOrderInfo(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        Assert.assertNull(resp.getData().getReceiver());
    }



    @Test
    public void t003_下单发凭证() throws Exception {
        createEvOrder();
    }

    @Test
    public void t004_电子凭证_完整链路() throws Exception {
        MainOrder mainOrder = createEvOrder();

        // 查询子单1
        List<EvoucherDTO> evList1;
        {
            Long orderId = mainOrder.getSubOrders().get(0).getOrderId();
            EvoucherQueryRpcReq req = new EvoucherQueryRpcReq();
            req.setOrderId(orderId);
            req.setCustId(mainOrder.getCustomer().getCustId());
            RpcResponse<List<EvoucherDTO>> resp = evoucherFacade.queryEvouchers(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            evList1 = resp.getData();
            Assert.assertEquals(evList1.size(), 3);
        }

        // 核销子单1
        {
            int idx = 0;
            for (EvoucherDTO ev : evList1) {
                EvoucherModifyRpcReq req = new EvoucherModifyRpcReq();
                req.setEvCode(ev.getEvCode());
                req.setSellerId(mainOrder.getSeller().getSellerId());
                RpcResponse resp = evoucherFacade.writeOff(req);
                printPretty(resp);
                Assert.assertTrue(resp.isSuccess());

                mainOrder = orderQueryAbility.getMainOrder(mainOrder.getPrimaryOrderId());
                Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_SENDED.getCode());
                Integer subStatus = mainOrder.getSubOrders().get(0).getOrderStatus();
                if (++idx < 3) {
                    Assert.assertEquals(subStatus, OrderStatusEnum.ORDER_SENDED.getCode());
                } else {
                    Assert.assertEquals(subStatus, OrderStatusEnum.ORDER_CONFIRM.getCode());
                }
            }
        }

        // 查询子单2
        List<EvoucherDTO> evList2;
        {
            Long orderId = mainOrder.getSubOrders().get(1).getOrderId();
            EvoucherQueryRpcReq req = new EvoucherQueryRpcReq();
            req.setOrderId(orderId);
            req.setCustId(mainOrder.getCustomer().getCustId());
            RpcResponse<List<EvoucherDTO>> resp = evoucherFacade.queryEvouchers(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            evList2 = resp.getData();
            Assert.assertEquals(evList1.size(), 3);
        }

        // 核销子单2
        {
            int idx = 0;
            for (EvoucherDTO ev : evList2) {
                EvoucherModifyRpcReq req = new EvoucherModifyRpcReq();
                req.setEvCode(ev.getEvCode());
                req.setSellerId(mainOrder.getSeller().getSellerId());
                RpcResponse resp = evoucherFacade.writeOff(req);
                printPretty(resp);
                Assert.assertTrue(resp.isSuccess());

                mainOrder = orderQueryAbility.getMainOrder(mainOrder.getPrimaryOrderId());
                Integer subStatus = mainOrder.getSubOrders().get(1).getOrderStatus();
                if (++idx < 3) {
                    Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_SENDED.getCode());
                    Assert.assertEquals(subStatus, OrderStatusEnum.ORDER_SENDED.getCode());
                } else {
                    Assert.assertEquals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_CONFIRM.getCode());
                    Assert.assertEquals(subStatus, OrderStatusEnum.ORDER_CONFIRM.getCode());
                }
            }
        }
    }
}
