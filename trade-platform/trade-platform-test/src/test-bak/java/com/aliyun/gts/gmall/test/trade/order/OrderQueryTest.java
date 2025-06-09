package com.aliyun.gts.gmall.test.platform.order;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderSearchRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.test.platform.base.BaseOrderTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class OrderQueryTest extends BaseOrderTest {

    @Autowired
    private OrderReadFacade orderReadFacade;

    @Test
    public void t001_查询() {
        OrderSearchRpcReq req = new OrderSearchRpcReq();
        req.setMainTags(Arrays.asList("CAMP_TOOL_manzeng"));
        RpcResponse<PageInfo<MainOrderDTO>> resp = orderReadFacade.queryOrderList(req);
        printPretty(resp);
        Assert.assertTrue(resp.isSuccess());
        PageInfo<MainOrderDTO> page = resp.getData();
        Assert.assertTrue(page.getTotal() > 0);
        Assert.assertFalse(page.getList().isEmpty());

        for (MainOrderDTO mainOrder : page.getList()) {
            Assert.assertTrue(mainOrder.getTags().contains("CAMP_TOOL_manzeng"));
        }
    }
}
