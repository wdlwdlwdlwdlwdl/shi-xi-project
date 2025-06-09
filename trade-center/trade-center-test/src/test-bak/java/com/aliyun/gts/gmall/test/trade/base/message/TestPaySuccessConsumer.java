package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.util.Objects;

@Slf4j
@MQConsumer(groupId = TestConstants.UNIT_TEST_MESSAGE_GID,
        topic = "${trade.pay.success.topic}",
        tag = "${trade.pay.success.tag}")
@Ignore
public class TestPaySuccessConsumer extends BaseTestConsumer<PaySuccessMessage> {

    public static void waitMessage(Long primaryOrderId, String payId) {
        getInstance(TestPaySuccessConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryOrderId(), primaryOrderId.toString())
                        && Objects.equals(dto.getPayId(), payId.toString())
        );
    }
}
