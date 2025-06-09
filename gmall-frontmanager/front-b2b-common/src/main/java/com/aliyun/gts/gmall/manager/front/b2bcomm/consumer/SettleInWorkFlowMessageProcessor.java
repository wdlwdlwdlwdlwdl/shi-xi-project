package com.aliyun.gts.gmall.manager.front.b2bcomm.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@MQConsumer(groupId = "${mq.consumer.workFlow.groupId}",
        topic = "${mq.consumer.workFlow.topic}")
@Slf4j(topic = "business.log")
public class SettleInWorkFlowMessageProcessor implements ConsumeEventProcessor {
    @Autowired
    private List<ConsumerService> consumerServices;

    private Map<String, ConsumerService> consumerServiceContainer;

    @Override
    public boolean process(StandardEvent event) {
        log.info("receive-mq-message={}", JSONObject.toJSONString(event));
        WorkflowInvovedMessage message = (WorkflowInvovedMessage) event.getPayload().getData();
        ConsumerService consumerService = getConsumerService(event.getTag());
        if(consumerService==null) {
            log.error("该消息无法处理,event={}", event);
            return true;
        }
        try {
            consumerService.consume(message);
            return true;
        } catch(Exception e) {
            log.error("message-process-failed, error={},event={}", e,event);
        }
        return false;
    }

    private ConsumerService getConsumerService(String tag) {
        if(MapUtils.isEmpty(consumerServiceContainer)) {
            consumerServiceContainer = consumerServices.stream()
                    .collect(Collectors.toMap(ConsumerService::getTag, Function.identity()));
        }
        return consumerServiceContainer.get(tag);
    }
}
