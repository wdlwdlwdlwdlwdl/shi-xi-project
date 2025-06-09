package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

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

/**
 * 订单创建 step 18
 *     下单结果解析
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewResultHandler extends AdapterHandler<TOrderCreate> {

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        List<PrimaryOrderResultDTO> list = order.getMainOrders()
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
        CreateOrderResultDTO result = new CreateOrderResultDTO();
        result.setOrders(list);
        inbound.setResult(result);
    }

    /**
     * 返回结果解析
     * @param mainOrder
     * @return
     */
    private PrimaryOrderResultDTO convert(MainOrder mainOrder) {
        PayPrice price = mainOrder.getCurrentPayInfo().getPayPrice();
        PrimaryOrderResultDTO primaryOrderResultDTO = new PrimaryOrderResultDTO();
        primaryOrderResultDTO.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        primaryOrderResultDTO.setPayPointAmt(price.getPointAmt());
        primaryOrderResultDTO.setPayRealAmt(price.getOrderRealAmt());
        return primaryOrderResultDTO;
    }
}
