package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.checkout;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CheckOutCreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * 返回结果转换
 */
@Component
public class CreateCheckOutOrderNewResultHandler extends TradeFlowHandler.AdapterHandler<TOrderCheckOutCreate> {

    @Override
    public void handle(TOrderCheckOutCreate inbound) {
        CheckOutCreatingOrder checkOutCreatingOrder = inbound.getDomain();
        CreatingOrder creatingOrder = checkOutCreatingOrder.getCreatingOrder();
        CheckOutOrderResultDTO checkOutOrderResultDTO = new CheckOutOrderResultDTO();
        checkOutOrderResultDTO.setCreatedSuccess(checkOutCreatingOrder.getCreatedSuccess());
        checkOutOrderResultDTO.setCheckSuccess(checkOutCreatingOrder.getCheckSuccess());
        if (creatingOrder != null && CollectionUtils.isNotEmpty(creatingOrder.getOriginMainOrderList())) {
            checkOutOrderResultDTO.setOriginMainOrderList(creatingOrder.getOriginMainOrderList());
        }
        inbound.setResult(checkOutOrderResultDTO);
    }
}
