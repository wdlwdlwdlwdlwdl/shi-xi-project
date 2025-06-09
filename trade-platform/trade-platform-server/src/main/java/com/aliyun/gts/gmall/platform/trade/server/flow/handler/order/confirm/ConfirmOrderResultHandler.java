package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderResultHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private TcOrderConverter tcOrderConverter;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderDTO result = tcOrderConverter.toConfirmOrderDTO(inbound.getDomain(), inbound);
        inbound.setResult(result);
    }
}
