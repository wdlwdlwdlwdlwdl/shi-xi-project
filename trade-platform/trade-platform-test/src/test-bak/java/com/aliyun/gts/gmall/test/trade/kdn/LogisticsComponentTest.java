package com.aliyun.gts.gmall.test.platform.kdn;

import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsFlowDTO;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
import com.aliyun.gts.gmall.middleware.logistics.impl.KdnLogisticsComponent;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LogisticsComponentTest extends BaseTest {

    @Autowired
    private LogisticsComponent logisticsComponent;

    @Test
    public void test() {
        LogisticsQueryReq req = new LogisticsQueryReq();
        req.setCompanyCode("ANE");
        req.setLogisticsCode("210001633605");
        List<LogisticsFlowDTO> list = logisticsComponent.queryTrace(req);
        list.stream().forEach(System.out::println);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void test_2() {
        KdnLogisticsComponent kdn = new KdnLogisticsComponent();
        Class c = KdnLogisticsComponent.class;
        setField(c, kdn, "appKey", "621aba39-83aa-41ed-af88-72ded0561447");
        setField(c, kdn, "eBusinessId", "test1701397");
        setField(c, kdn, "host", "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json");
        setField(c, kdn, "requestType", "1002");

        LogisticsQueryReq req = new LogisticsQueryReq();
        req.setCompanyCode("ANE");
        req.setLogisticsCode("210001633605");
        List<LogisticsFlowDTO> list = kdn.queryTrace(req);
        System.out.println("--------------------");
        list.stream().forEach(System.out::println);
        Assert.assertTrue(list.size() > 0);
    }

    // b+ 测试账号
    @Test
    public void test_3() {
        KdnLogisticsComponent kdn = new KdnLogisticsComponent();
        Class c = KdnLogisticsComponent.class;
        setField(c, kdn, "appKey", "ba5f0a4b-f12b-4c2a-8e00-5d2ac597658c");
        setField(c, kdn, "eBusinessId", "test1713503");
        setField(c, kdn, "host", "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json");
        setField(c, kdn, "requestType", "1002");

        LogisticsQueryReq req = new LogisticsQueryReq();
        req.setCompanyCode("ANE");
        req.setLogisticsCode("210001633605");
        List<LogisticsFlowDTO> list = kdn.queryTrace(req);
        System.out.println("--------------------");
        list.stream().forEach(System.out::println);
        Assert.assertTrue(list.size() > 0);
    }

}
