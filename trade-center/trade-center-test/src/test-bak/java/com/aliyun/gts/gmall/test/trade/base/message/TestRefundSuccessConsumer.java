package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.util.Objects;

@Slf4j
@MQConsumer(groupId = TestConstants.UNIT_TEST_MESSAGE_GID,
        topic = "${trade.pay.refund.topic}",
        tag = "${trade.pay.refund.tag}")
@Ignore
public class TestRefundSuccessConsumer extends BaseTestConsumer<RefundSuccessMessage> {

    public static void waitMessage(String refundId) {
        getInstance(TestRefundSuccessConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getRefundId(), refundId)
        );
    }
}
