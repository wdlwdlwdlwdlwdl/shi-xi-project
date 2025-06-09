package com.aliyun.gts.gmall.test.platform.message;

import com.aliyun.gts.gmall.framework.api.dto.Transferable;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MqTest extends BaseTest {

    @Autowired
    private MessageSendManager messageSendManager;

    @Test
    public void t001_支付成功消息() {
        messageSendManager.sendMessage(new TestDTO("test"),
                "GMALL_TRADE_PAY_SUCCESS_TEST", "test");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestDTO implements Transferable {
        private String name;
    }
}
