package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ReversalMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

import java.util.Objects;

@Slf4j
@MQConsumer(groupId = TestConstants.UNIT_TEST_MESSAGE_GID,
        topic = "${trade.reversal.statuschange.topic}",
        tag = "1||10||11||20||100||101||102")
@Ignore
public class TestReversalStatusChangeConsumer extends BaseTestConsumer<ReversalMessageDTO> {

    public static void waitMessage(Long primaryReversalId, ReversalStatusEnum reversalStatus, ReversalStatusEnum fromReversalStatus) {
        getInstance(TestReversalStatusChangeConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryReversalId(), primaryReversalId)
                        && Objects.equals(dto.getReversalStatus(), reversalStatus.getCode())
                        && Objects.equals(dto.getFromReversalStatus(), fromReversalStatus==null?null:fromReversalStatus.getCode())
        );
    }
}
