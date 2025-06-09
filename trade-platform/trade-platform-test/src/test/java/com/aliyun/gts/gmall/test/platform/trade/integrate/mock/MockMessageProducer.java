package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import com.aliyun.gts.gmall.framework.api.mq.dto.MqPayload;
import com.aliyun.gts.gmall.middleware.api.mq.MessageProducer;
import com.aliyun.gts.gmall.middleware.api.mq.exception.MQException;
import com.aliyun.gts.gmall.middleware.api.mq.exception.MQTransactionException;
import com.aliyun.gts.gmall.middleware.api.mq.transaction.LocalTransactionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MockMessageProducer implements MessageProducer {

    @Autowired
    private MockMessageConsumer mockMessageConsumer;

    @Autowired(required = false)
    private List<LocalTransactionService> localTransactionServices;

    private static AtomicLong messageId = new AtomicLong(100);

    private static String nextId() {
        return "MESSAGE_" + messageId.getAndIncrement();
    }

    @Override
    public String send(String topic, String tag, MqPayload<? extends Transferable> payload) throws MQException {
        mockMessageConsumer.onSend(topic, tag, payload);
        return nextId();
    }

    @Override
    public String send(String topic, String tag, MqPayload<? extends Transferable> payload, Map<String, String> properties) throws MQException {
        return nextId();
    }

    @Override
    public String send(String topic, String tag, MqPayload<? extends Transferable> payload, long delayTime) throws MQException {
        return nextId();
    }

    @Override
    public String send(String topic, String tag, MqPayload<? extends Transferable> payload, long delayTime, Map<String, String> properties) throws MQException {
        return nextId();
    }

    @Override
    public String sendOrder(String topic, String tag, String shardingKey, MqPayload<? extends Transferable> payload) throws MQException {
        return nextId();
    }

    @Override
    public String sendOrder(String topic, String tag, String shardingKey, MqPayload<? extends Transferable> payload, Map<String, String> properties) throws MQException {
        return nextId();
    }

    @Override
    public String sendTransactional(String topic, String tag, MqPayload<? extends Transferable> payload) throws MQException {
        executeLocalTransaction(topic, tag, new HashMap<>());
        return nextId();
    }

    @Override
    public String sendTransactional(String topic, String tag, MqPayload<? extends Transferable> payload, Map<String, String> properties) throws MQException {
        executeLocalTransaction(topic, tag, properties);
        return nextId();
    }

    private void executeLocalTransaction(String topic, String tag, Map<String, String> properties) throws MQTransactionException {
        if (CollectionUtils.isEmpty(localTransactionServices)) {
            return;
        }
        for (LocalTransactionService local : localTransactionServices) {
            if (StringUtils.equals(local.getTopic(), topic)
                    && StringUtils.equals(local.getTag(), tag)) {
                local.executeTransaction(properties);
                return;
            }
        }
    }
}
