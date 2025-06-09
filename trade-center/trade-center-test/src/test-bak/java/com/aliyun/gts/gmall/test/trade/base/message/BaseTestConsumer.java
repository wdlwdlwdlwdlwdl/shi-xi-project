package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
public abstract class BaseTestConsumer<T> implements ConsumeEventProcessor {
    public static int WAIT_TIMES = 10;
    protected static int WAIT_INTERVAL = 1000;

    protected static Map<Class<? extends BaseTestConsumer>, BaseTestConsumer> instanceMap = new HashMap<>();
    {
        synchronized (instanceMap) {
            instanceMap.put(getClass(), this);
        }
    }

    protected static <X extends BaseTestConsumer> X getInstance(Class<X> clazz) {
        return (X) instanceMap.get(clazz);
    }

    public static void clearAll() {
        for (BaseTestConsumer consumer : instanceMap.values()) {
            synchronized (consumer.messagesLock) {
                consumer.messages.clear();
            }
        }
    }

    @Getter
    private List<T> messages = new ArrayList<>();
    private Object messagesLock = new Object();

    @Override
    public boolean process(StandardEvent event) {
        T dto = (T) event.getPayload().getData();
        log.warn("BaseTestConsumer.receive {}", dto);
        synchronized (messagesLock) {
            messages.add(dto);
        }
        return true;
    }

    protected int checkAndRemove(Predicate<T> p) {
        int hit = 0;
        synchronized (messagesLock) {
            List<T> newList = new ArrayList<>();
            for (T dto : messages) {
                if (p.test(dto)) {
                    hit++;
                } else {
                    newList.add(dto);
                }
            }
            this.messages = newList;
        }
        return hit;
    }

    protected void waitMessage(Predicate<T> p) {
        for (int i=0; i<WAIT_TIMES; i++) {
            try {
                Thread.sleep(WAIT_INTERVAL);
            } catch (InterruptedException e) {
                log.error("", e);
            }
            if (checkAndRemove(p) > 0) {
                return;
            }
        }
        Assert.fail("wait timeout, messages: " + messages);
    }
}
