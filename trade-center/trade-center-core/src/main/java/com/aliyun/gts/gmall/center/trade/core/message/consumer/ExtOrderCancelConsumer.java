package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGiftService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.core.message.consumer.OrderCancelConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@MQConsumer(
    groupId = "${trade.order.statuschange.groupId}",
    topic = "${trade.order.statuschange.topic}",
    tag = ExtOrderCancelConsumer.STATUS,
    priority = 9    // 覆盖 platform 中的 consumer
)
public class ExtOrderCancelConsumer implements ConsumeEventProcessor {

    static final String STATUS = "72";    // 未付款关闭: 买家取消 / 卖家关闭 / 系统关闭

    @Autowired
    private OrderCancelConsumer orderCancelConsumer;

    @Autowired
    private ManzengGiftService manzengGiftService;

    @Override
    public boolean process(StandardEvent event) {
        // platform 中的 consumer
        if (!orderCancelConsumer.process(event)) {
            return false;
        }
        OrderMessageDTO message = (OrderMessageDTO) event.getPayload().getData();
        manzengGiftService.onOrderCancel(message);
        return true;
    }
}
