package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单确认 step11
 *    订单确认计算结果转换返回
 */
@Component
public class ConfirmOrderNewResultHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private TcOrderConverter tcOrderConverter;

    @Override
    public void handle(TOrderConfirm inbound) {
        // 为了防止顺序变化 考虑订单排序问题
        ConfirmOrderDTO result = tcOrderConverter.toConfirmOrderDTO(inbound.getDomain(), inbound);
        inbound.setResult(result);
    }
}
