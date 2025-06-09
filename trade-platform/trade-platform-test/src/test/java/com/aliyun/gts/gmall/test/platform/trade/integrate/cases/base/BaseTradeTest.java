package com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.dto.ClientInfo;
import com.aliyun.gts.gmall.test.platform.trade.integrate.BaseIntegrateTest;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public abstract class BaseTradeTest extends BaseIntegrateTest {

    protected long nullZero(Long v) {
        return v == null ? 0 : v.longValue();
    }

    protected long nullZero(BigDecimal v) {
        return v == null ? 0 : v.longValue();
    }

    protected void printPretty(Object o) {
        System.out.println(JSON.toJSONString(o, true));
    }

    protected void printPretty(String name, Object o) {
        System.out.println(name + ": " + JSON.toJSONString(o, true));
    }

    protected ClientInfo getClientInfo() {
        return ClientInfo.builder().userId("xxx").build();
    }

    protected void setField(Class clazz, Object o, String name, Object value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(o, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
