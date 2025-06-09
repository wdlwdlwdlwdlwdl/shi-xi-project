package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.BaseOrderTest;
import org.junit.Test;
import org.testng.Assert;
import org.testng.collections.Sets;

import java.util.*;

public class OrderExtraTest extends BaseOrderTest {


    @Test
    public void t001_更新feature() {
        long primaryOrderId = createOrder();

        // 增加 feature
        {
            Map<String, String> feature = new HashMap<>();
            feature.put("test", "111");

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setAddFeatures(feature);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            MainOrder order = getMainOrder(primaryOrderId);
            Assert.assertEquals(order.getOrderAttr().getExtras().get("test"), "111");
        }

        // 修改 feature
        {
            Map<String, String> feature = new HashMap<>();
            feature.put("test", "222");

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setAddFeatures(feature);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            MainOrder order = getMainOrder(primaryOrderId);
            Assert.assertEquals(order.getOrderAttr().getExtras().get("test"), "222");
        }

        // 删除 feature
        {
            Set<String> feature = new HashSet<>();
            feature.add("test");

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setRemoveFeatures(feature);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            MainOrder order = getMainOrder(primaryOrderId);
            Assert.assertNull(order.getOrderAttr().getExtras().get("test"));
        }
    }

    @Test
    public void t002_更新扩展结构() {
        long primaryOrderId = createOrder();

        // 增加 extend
        {
            Map<String, String> props = new HashMap<>();
            props.put("test", "111");
            Map<String, Map<String, String>> extend = new HashMap<>();
            extend.put("test", props);

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setAddExtends(extend);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            OrderExtendQueryRpcReq q = new OrderExtendQueryRpcReq();
            q.setPrimaryOrderId(primaryOrderId);
            q.setExtendType("test");
            q.setExtendKey("test");
            RpcResponse<List<OrderExtendDTO>> result = orderReadFacade.queryOrderExtend(q);
            printPretty(result);
            Assert.assertTrue(result.isSuccess());
            Assert.assertEquals(result.getData().size(), 1);
            Assert.assertEquals(result.getData().get(0).getExtendValue(), "111");
        }

        // 修改 extend
        {
            Map<String, String> props = new HashMap<>();
            props.put("test", "222");
            Map<String, Map<String, String>> extend = new HashMap<>();
            extend.put("test", props);

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setAddExtends(extend);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            OrderExtendQueryRpcReq q = new OrderExtendQueryRpcReq();
            q.setPrimaryOrderId(primaryOrderId);
            q.setExtendType("test");
            q.setExtendKey("test");
            RpcResponse<List<OrderExtendDTO>> result = orderReadFacade.queryOrderExtend(q);
            printPretty(result);
            Assert.assertTrue(result.isSuccess());
            Assert.assertEquals(result.getData().size(), 1);
            Assert.assertEquals(result.getData().get(0).getExtendValue(), "222");
        }

        // 删除 extend
        {
            Map<String, Set<String>> extend = new HashMap<>();
            extend.put("test", Sets.newHashSet(Arrays.asList("test")));

            OrderExtraSaveRpcReq req = new OrderExtraSaveRpcReq();
            req.setPrimaryOrderId(primaryOrderId);
            req.setRemoveExtendKeys(extend);
            RpcResponse resp = orderWriteFacade.saveOrderExtras(req);
            printPretty(resp);
            Assert.assertTrue(resp.isSuccess());

            OrderExtendQueryRpcReq q = new OrderExtendQueryRpcReq();
            q.setPrimaryOrderId(primaryOrderId);
            q.setExtendType("test");
            q.setExtendKey("test");
            RpcResponse<List<OrderExtendDTO>> result = orderReadFacade.queryOrderExtend(q);
            printPretty(result);
            Assert.assertTrue(result.isSuccess());
            Assert.assertEquals(result.getData().size(), 0);
        }
    }
}
