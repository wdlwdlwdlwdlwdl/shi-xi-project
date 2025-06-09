package com.aliyun.gts.gmall.test.platform.reversal;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.SubReversalDTO;
import com.aliyun.gts.gmall.test.platform.base.BaseReversalTest;
import org.junit.Assert;
import org.junit.Test;

public class ReversalSearchTest extends BaseReversalTest {
    private static final long CUST_ID = 401L;
    private static final long SELLER_ID = 1L;

    @Test
    public void r001_客户查询() {
        ReversalQueryRpcReq req = new ReversalQueryRpcReq();
        req.setCustId(CUST_ID);
        req.setPage(new PageParam(1, 10));
        RpcResponse<PageInfo<MainReversalDTO>> resp = reversalReadFacade.queryReversalList(req);
        printPretty(resp);
        assertSuccess(resp);
    }

    @Test
    public void r002_卖家查询() {
        ReversalQueryRpcReq req = new ReversalQueryRpcReq();
        req.setSellerId(SELLER_ID);
        req.setPage(new PageParam(1, 10));
        RpcResponse<PageInfo<MainReversalDTO>> resp = reversalReadFacade.queryReversalList(req);
        printPretty(resp);
        assertSuccess(resp);
    }

    @Test
    public void r003_商品标题查询() {
        ReversalQueryRpcReq req = new ReversalQueryRpcReq();
        req.setSellerId(SELLER_ID);
        req.setItemTitle("测试");
        req.setPage(new PageParam(1, 10));
        RpcResponse<PageInfo<MainReversalDTO>> resp = reversalReadFacade.queryReversalList(req);
        printPretty(resp);
        assertSuccess(resp);
    }

    private void assertSuccess(RpcResponse<PageInfo<MainReversalDTO>> resp) {
        Assert.assertTrue(resp.isSuccess());

        PageInfo<MainReversalDTO> page = resp.getData();
        Assert.assertNotNull(page);
        Assert.assertTrue(page.getTotal() > 0);
        Assert.assertTrue(page.getList().size() > 0);

        for (MainReversalDTO re : page.getList()) {
            Assert.assertNotNull(re.getCancelAmt());
            Assert.assertNotNull(re.getCancelQty());
            Assert.assertNotNull(re.getCustId());
            Assert.assertNotNull(re.getCustName());
            //Assert.assertNotNull(re.getItemReceived());
            Assert.assertNotNull(re.getPrimaryOrderId());
            Assert.assertNotNull(re.getReversalChannel());
            Assert.assertNotNull(re.getReversalReasonCode());
            Assert.assertNotNull(re.getReversalStatus());
            Assert.assertNotNull(re.getReversalType());
            Assert.assertNotNull(re.getSubReversals());
            Assert.assertTrue(re.getSubReversals().size() > 0);

            for (SubReversalDTO sub : re.getSubReversals()) {
                Assert.assertNotNull(sub.getCancelAmt());
                Assert.assertNotNull(sub.getCancelQty());
                Assert.assertNotNull(sub.getOrderId());
                Assert.assertNotNull(sub.getPrimaryReversalId());
                Assert.assertNotNull(sub.getReversalId());
            }
        }
    }
}
