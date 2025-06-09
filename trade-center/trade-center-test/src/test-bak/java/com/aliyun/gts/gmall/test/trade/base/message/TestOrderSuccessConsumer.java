package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.util.Objects;

@Slf4j
@MQConsumer(
    groupId = TestConstants.UNIT_TEST_MESSAGE_GID,
    topic = "${trade.order.ordersuccess.topic}",
    tag = "SUCCESS"
)
@Ignore
public class TestOrderSuccessConsumer extends BaseTestConsumer<OrderMessageDTO> {

    public static void waitMessage(Long primaryOrderId, OrderStatusEnum primaryOrderStatus) {
        getInstance(TestOrderSuccessConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryOrderId(), primaryOrderId)
                        && Objects.equals(dto.getPrimaryOrderStatus(), primaryOrderStatus.getCode())
        );
    }
}
