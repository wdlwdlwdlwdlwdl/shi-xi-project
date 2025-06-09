package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerIdRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.TradeConfigFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OversellProcessType;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.SystemReversalReason;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.OrderConfigServiceImpl;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseOrderTest;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.TestConstants;
import com.aliyun.gts.gmall.test.platform.trade.integrate.mock.MockPayMessage;
import com.aliyun.gts.gmall.test.platform.trade.util.TestUtils;
import com.google.common.cache.Cache;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderOversaleTest extends BaseOrderTest {
    private static final long SELLER_ID = TestConstants.SELLER_ID;

    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private TradeConfigFacade tradeConfigFacade;
    @Autowired
    private MockPayMessage mockPayMessage;
    @Autowired
    private OrderConfigService orderConfigService;

    private void setOversellType(OversellProcessType type, ThrowRunnable runnable) throws Exception {
        Cache cache = TestUtils.getPrivateField(orderConfigService,
                OrderConfigServiceImpl.class, "cache");
        cache.invalidateAll();

        Integer oldType;
        {
            SellerIdRpcReq req = new SellerIdRpcReq();
            req.setSellerId(SELLER_ID);
            RpcResponse<SellerConfigDTO> resp = tradeConfigFacade.getSellerConfig(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            oldType = resp.getData().getOversellProcessType();
        }
        if (oldType.intValue() == type.getCode().intValue()) {
            runnable.run();
        }

        try {
            SellerConfigDTO up = new SellerConfigDTO();
            up.setSellerId(SELLER_ID);
            up.setOversellProcessType(type.getCode());
            RpcResponse resp = tradeConfigFacade.saveSellerConfig(up);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            runnable.run();
        } finally {
            // 恢复超卖处理方式
            SellerConfigDTO up = new SellerConfigDTO();
            up.setSellerId(SELLER_ID);
            up.setOversellProcessType(oldType);
            RpcResponse resp = tradeConfigFacade.saveSellerConfig(up);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }
    }

    @Test
    public void t001_超卖消息() throws Exception {
        setOversellType(OversellProcessType.AUTO_CLOSE, () -> {
            long pOrderId = createOrder();
            toPay(pOrderId);

            // 超卖校验环节, 直接发送超卖消息
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                    pOrderId, OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode(), null, null);
            mockPayMessage.sendOverSale(pOrderId);
            //sleep(3000L);

            List<MainReversal> mainReversals = reversalQueryService.queryReversalByOrder(pOrderId);
            Assert.assertNotNull(mainReversals);
            Assert.assertEquals(1, mainReversals.size());
            MainReversal mainReversal = mainReversals.get(0);
            Assert.assertEquals(ReversalStatusEnum.WAIT_REFUND.getCode(), mainReversal.getReversalStatus());

            MainOrder mainOrder = getMainOrder(pOrderId);
            Assert.assertNotNull(mainOrder.getOrderAttr().getOverSale());
            Assert.assertTrue(mainOrder.getOrderAttr().getOverSale().booleanValue());

            // mock 退款成功消息, 本地测试收不到dev消息
            //sleep(3000L);
            mockPayMessage.sendRefundSuccess(mainReversal, null);
            //sleep(3000L);

            mainOrder = getMainOrder(pOrderId);
            Assert.assertEquals(OrderStatusEnum.REVERSAL_SUCCESS.getCode(), mainOrder.getPrimaryOrderStatus());
            List<MainReversal> reversals = reversalQueryService.queryReversalByOrder(pOrderId);
            Assert.assertEquals(reversals.size(), 1);
            MainReversal reversal = reversals.get(0);
            Assert.assertEquals(reversal.getReversalStatus().intValue(), ReversalStatusEnum.REVERSAL_OK.getCode().intValue());
            Assert.assertEquals(reversal.getReversalReasonCode().intValue(), SystemReversalReason.OVER_SALE.getCode().intValue());
        });
    }

    private interface ThrowRunnable {
        void run() throws Exception;
    }
}
