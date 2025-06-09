package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseReversalTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class OrderProcessTest extends BaseReversalTest {

    @Test
    public void t001_正向流程() {
        // 下单、支付
        List<Long> ids = createOrd(null, Long.MAX_VALUE);

        // 发货
        {
            LogisticsInfoRpcReq logis = new LogisticsInfoRpcReq();
            logis.setLogisticsId("aaa");
            logis.setCompanyType(1);

            TcLogisticsRpcReq req = new TcLogisticsRpcReq();
            req.setCustId(CUST_ID);
            req.setPrimaryOrderId(ids.get(0));
            req.setSellerId(SELLER_ID);
            req.setInfoList(Arrays.asList(logis));
            req.setReceiverPhone("xx");
            req.setReceiverName("xx");
            req.setReceiverAddr("xx");
            req.setType(LogisticsTypeEnum.REALITY.getCode());

            RpcResponse resp = orderWriteFacade.sendOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }

        // 确认收货
        {
            PrimaryOrderRpcReq req = new PrimaryOrderRpcReq();
            req.setCustId(CUST_ID);
            req.setPrimaryOrderId(ids.get(0));
            RpcResponse resp = orderWriteFacade.confirmReceiveOrder(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }
    }
}
