package com.aliyun.gts.gmall.test.platform.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerIdRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.TradeConfigFacade;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TradeConfigTest extends BaseTest {
    private static final Long SELLER_ID = 123L;

    @Autowired
    private TradeConfigFacade tradeConfigFacade;

    @Test
    public void t001_保存_查询() {
        int value = (int) (Math.random() * 10000L);

        {
            SellerConfigDTO req = new SellerConfigDTO();
            req.setSellerId(SELLER_ID);
            req.setAutoCloseOrderTimeInSec(value);
            RpcResponse<SellerConfigDTO> resp = tradeConfigFacade.saveSellerConfig(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
        }

        {
            SellerIdRpcReq req = new SellerIdRpcReq();
            req.setSellerId(SELLER_ID);
            RpcResponse<SellerConfigDTO> resp = tradeConfigFacade.getSellerConfig(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());
            Assert.assertEquals(resp.getData().getAutoCloseOrderTimeInSec().intValue(), value);
        }
    }
}
