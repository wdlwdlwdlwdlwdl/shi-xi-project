package com.aliyun.gts.gmall.test.platform.facade;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.PrimaryOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.collections.Lists;

public class OrderWriteFacadeTest extends BaseTest {

    @Autowired
    OrderWriteFacade orderWriteFacade;


    @Test
    public void testSend(){
        TcLogisticsRpcReq tcLogisticsRpcReq = new TcLogisticsRpcReq();
        tcLogisticsRpcReq.setCustId(100000L);
        tcLogisticsRpcReq.setSellerId(1L);
        tcLogisticsRpcReq.setPrimaryOrderId(350010000000L);
        tcLogisticsRpcReq.setReceiverAddr("xx省xx市xx区xxxx");
        tcLogisticsRpcReq.setReceiverName("aaa");
        tcLogisticsRpcReq.setReceiverPhone("15000000000");
        tcLogisticsRpcReq.setType(LogisticsTypeEnum.REALITY.getCode());

        LogisticsInfoRpcReq logisticsInfoRpcReq = new LogisticsInfoRpcReq();
        logisticsInfoRpcReq.setCompanyType(LogisticsCompanyTypeEnum.ANE.getCode());
        logisticsInfoRpcReq.setLogisticsId("210001633605");

        tcLogisticsRpcReq.setInfoList(Lists.newArrayList(logisticsInfoRpcReq));

        orderWriteFacade.sendOrder(tcLogisticsRpcReq);

        System.out.println();

    }

    @Test
    public void testReceive(){
        PrimaryOrderRpcReq primaryOrderRpcReq = new PrimaryOrderRpcReq();
        primaryOrderRpcReq.setCustId(100000L);
        primaryOrderRpcReq.setPrimaryOrderId(350010000000L);

        orderWriteFacade.confirmReceiveOrder(primaryOrderRpcReq);
    }

}
