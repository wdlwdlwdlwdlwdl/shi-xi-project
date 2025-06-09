package com.aliyun.gts.gmall.test.platform.base;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.dto.ClientInfo;
import com.aliyun.gts.gmall.platform.bootstrap.Bootstrap;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootstrap.class)
public abstract class BaseTest {

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
