package com.aliyun.gts.gmall.manager.front;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import com.aliyun.gts.gmall.framework.api.mq.dto.MqPayload;
import com.aliyun.gts.gmall.middleware.api.mq.MessageProducer;
import com.aliyun.gts.gmall.middleware.api.mq.exception.MQException;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xinchen
 * 消息发送
 */
@Slf4j
@Component
public class MessageSendManager {
    @Autowired
    private MessageProducer messageProducer;

    /**
     * 发送同步无序消息
     *
     * @param message 消息
     * @param topic
     * @param tag
     * @param <T>     数据
     * @return
     */
    public <T extends Transferable> boolean sendMessage(T message, String topic, String tag) {
        try {
            MqPayload<T> payload = MqPayload.make(message);
            String messageId = messageProducer.send(topic, tag, payload);
            log.info("topic: {},tag: {},交易消息ID: {},success to send message 消息发送成功: {}", topic, tag, messageId, JSONObject.toJSONString(message, true));
            return true;
        } catch (MQException e) {
            log.error("topic: {},tag: {},message: {},消息发送失败,异常信息: {}", topic, tag,
                    JSONObject.toJSONString(message, true), Throwables.getStackTraceAsString(e));
            return false;
        }
    }

    /**
     * 发送延迟无序消息
     *
     * @param message
     * @param topic
     * @param tag
     * @param delayTime
     * @param <T>
     * @return
     */
    public <T extends Transferable> boolean sendMessageWithDeplay(T message, String topic, String tag, long delayTime) {
        try {
            MqPayload<T> payload = MqPayload.make(message);
            String messageId = messageProducer.send(topic, tag, payload, delayTime);
            log.info("topic: {},交易消息ID: {},success to send message 消息发送成功: {}", topic, messageId, JSONObject.toJSONString(message, true));
            return true;
        } catch (MQException e) {
            log.error("topic: {},tag: {},message: {},延迟消息发送失败,异常信息: {}", topic, tag,
                    JSONObject.toJSONString(message, true), Throwables.getStackTraceAsString(e));
            return false;
        }
    }

    /**
     * 发送同步顺序消息
     *
     * @param message
     * @param topic
     * @param tag
     * @param shardingKey
     * @param <T>
     * @return
     */
    public <T extends Transferable> boolean sendMessageWithOrder(T message, String topic, String tag, String shardingKey) {
        try {
            MqPayload<T> payload = MqPayload.make(message);
            String messageId = messageProducer.sendOrder(topic, tag, shardingKey, payload);
            log.info("topic: {},tag: {},交易消息ID: {},success to send message 消息发送成功: {}", topic, tag, messageId, JSONObject.toJSONString(message, true));
            return true;
        } catch (Exception e) {
            log.error("topic: {},tag: {},message: {},顺序消息发送失败,异常信息: {}", topic, tag,
                    JSONObject.toJSONString(message, true), Throwables.getStackTraceAsString(e));
            return false;
        }
    }

}
