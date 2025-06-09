package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.core.domainservice.TradeNoticeService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ReversalMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@MQConsumer(
    groupId = "${trade.reversal.statuschange.groupId}",
    topic = "${trade.reversal.statuschange.topic}",
    tag = ReversalCreateConsumer.STATUS
)
public class ReversalCreateConsumer implements ConsumeEventProcessor {

    static final String STATUS = "1"; // @see ReversalStatusEnum

    @Autowired
    private TradeNoticeService tradeNoticeService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("receive event msg = " + JSONObject.toJSONString(event));
        ReversalMessageDTO message = (ReversalMessageDTO) event.getPayload().getData();
        // 提醒消息
        tradeNoticeService.onReversalCreate(message.getPrimaryReversalId());
        return true;
    }
}
