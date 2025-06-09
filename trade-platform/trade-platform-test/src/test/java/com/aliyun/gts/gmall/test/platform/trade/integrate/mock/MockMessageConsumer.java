package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import com.aliyun.gts.gmall.framework.api.mq.dto.MqPayload;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.middleware.mq.model.DefaultStandardEvent;
import com.aliyun.gts.gmall.platform.trade.core.message.consumer.ToRefundConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MockMessageConsumer {
    @Value("${trade.reversal.torefund.topic:GMALL_TRADE_REVERSAL_TO_REFUND}")
    private String toRefundTopic;

    @Autowired
    private ToRefundConsumer toRefundConsumer;

    public void onSend(String topic, String tag, MqPayload<? extends Transferable> payload) {
        if (toRefundTopic.equals(topic) && MessageConstant.TRADE_PAY_REFUND.equals(tag)) {
            StandardEvent event = new DefaultStandardEvent(topic, tag);
            event.setPayload(payload);
            toRefundConsumer.process(event);
        }
    }
}
