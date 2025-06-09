package com.aliyun.gts.gmall.test.trade.base.message;

import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.test.trade.base.TestConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.Objects;

@Slf4j
@MQConsumer(groupId = TestConstants.UNIT_TEST_MESSAGE_GID,
        topic = "${trade.order.statuschange.topic}",
        tag = "10||11||12||20||21||25||27||30||33||34||35||36||37||40")
@Ignore
public class TestOrderStatusChangeConsumer extends BaseTestConsumer<OrderMessageDTO> {

    public static void waitMessage(Long primaryOrderId, OrderStatusEnum primaryOrderStatus) {
        Assert.assertNotEquals(primaryOrderStatus, OrderStatusEnum.STEP_ORDER_DOING);
        getInstance(TestOrderStatusChangeConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryOrderId(), primaryOrderId)
                        && Objects.equals(dto.getOrderId(), primaryOrderId)
                        && Objects.equals(dto.getPrimaryOrderStatus(), primaryOrderStatus.getCode())
                        && Objects.equals(dto.isChangePrice(), false)
        );
    }

    public static void waitMessage(Long primaryOrderId, int stepNo, StepOrderStatusEnum stepStatus) {
        getInstance(TestOrderStatusChangeConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryOrderId(), primaryOrderId)
                        && Objects.equals(dto.getOrderId(), primaryOrderId)
                        && Objects.equals(dto.getPrimaryOrderStatus(), OrderStatusEnum.STEP_ORDER_DOING.getCode())
                        && Objects.equals(dto.getStepNo(), stepNo)
                        && Objects.equals(dto.getStepOrderStatus(), stepStatus.getCode())
                        && Objects.equals(dto.isChangePrice(), false)
        );
    }

    public static void waitChangePriceMessage(Long primaryOrderId, int stepNo, StepOrderStatusEnum stepStatus) {
        getInstance(TestOrderStatusChangeConsumer.class).waitMessage(dto ->
                Objects.equals(dto.getPrimaryOrderId(), primaryOrderId)
                        && Objects.equals(dto.getOrderId(), primaryOrderId)
                        && Objects.equals(dto.getPrimaryOrderStatus(), OrderStatusEnum.STEP_ORDER_DOING.getCode())
                        && Objects.equals(dto.getStepNo(), stepNo)
                        && Objects.equals(dto.getStepOrderStatus(), stepStatus.getCode())
                        && Objects.equals(dto.isChangePrice(), true)
        );
    }
}
