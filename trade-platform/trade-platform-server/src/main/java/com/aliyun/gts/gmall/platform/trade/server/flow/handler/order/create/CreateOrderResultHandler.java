package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.PrimaryOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreateOrderResultHandler extends AdapterHandler<TOrderCreate> {

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        List<PrimaryOrderResultDTO> list = order.getMainOrders().stream()
                .map(this::convert).collect(Collectors.toList());

        CreateOrderResultDTO result = new CreateOrderResultDTO();
        result.setOrders(list);
        inbound.setResult(result);
    }

    private PrimaryOrderResultDTO convert(MainOrder mainOrder) {
        PayPrice price = mainOrder.getCurrentPayInfo().getPayPrice();
        PrimaryOrderResultDTO p = new PrimaryOrderResultDTO();
        p.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        p.setPayPointAmt(price.getPointAmt());
        p.setPayRealAmt(price.getOrderRealAmt());
        return p;
    }
}
