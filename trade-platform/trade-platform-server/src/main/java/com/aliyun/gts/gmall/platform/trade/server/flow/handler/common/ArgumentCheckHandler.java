package com.aliyun.gts.gmall.platform.trade.server.flow.handler.common;

import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.stereotype.Component;

@Component
public class ArgumentCheckHandler extends AdapterHandler<AbstractContextEntity> {

    @Override
    public void handle(AbstractContextEntity inbound) {
        inbound.getReq().checkInput();
    }
}
