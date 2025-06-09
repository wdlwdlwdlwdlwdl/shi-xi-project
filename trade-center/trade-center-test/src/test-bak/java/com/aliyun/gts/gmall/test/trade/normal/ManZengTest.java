package com.aliyun.gts.gmall.test.trade.normal;

import com.aliyun.gts.gmall.center.trade.core.domainservice.impl.ManzengGrantServiceImpl;
import com.aliyun.gts.gmall.test.trade.base.BaseOrderTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/12 14:13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ManZengTest extends BaseOrderTest {
    @Autowired
    private ManzengGrantServiceImpl manzengGrantService;

    @Test
    public void t012_满赠测试() {
        manzengGrantService.processSendSucc(13340010000007L);
    }

    @Test
    public void t013_满赠送商品测试() {
        manzengGrantService.test(13340010000007L);
    }
}
