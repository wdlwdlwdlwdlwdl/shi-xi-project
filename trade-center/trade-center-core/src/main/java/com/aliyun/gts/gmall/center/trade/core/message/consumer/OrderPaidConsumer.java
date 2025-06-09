package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.aliyun.gts.gmall.center.trade.core.domainservice.TradeNoticeService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.core.message.consumer.PaySuccessConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@MQConsumer(
    groupId = "${trade.pay.success.groupId}",
    topic = "${trade.pay.success.topic}",
    tag = "${trade.pay.success.tag}",
    priority = 9    // 覆盖 platform 中的 consumer
)
public class OrderPaidConsumer implements ConsumeEventProcessor {

    @Autowired
    private TradeNoticeService tradeNoticeService;

    @Autowired
    private PaySuccessConsumer paySuccessConsumer;

    @Override
    public boolean process(StandardEvent event) {
        // platform 中的 consumer
        if (!paySuccessConsumer.process(event)) {
            return false;
        }
        PaySuccessMessage message = (PaySuccessMessage) event.getPayload().getData();
        // 提醒消息
        tradeNoticeService.onOrderPaid(message);
        return true;
    }
}
