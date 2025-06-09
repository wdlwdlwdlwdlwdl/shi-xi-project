package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderReceiverAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ConfirmOrderReceiverHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderReceiverAbility orderReceiverAbility;
    @Autowired
    private TcOrderConverter tcOrderConverter;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();
        for(MainOrder mainOrder:order.getMainOrders()){
            for(SubOrder subOrder :mainOrder.getSubOrders()){
                Optional<ConfirmItemInfo> item = req.getOrderItems().stream()
                        .filter(p-> Objects.equals(p.getSkuId(), subOrder.getItemSku().getSkuId())).findAny();
                if(item.isPresent()){
                    ReceiveAddr addr = tcOrderConverter.toReceiveAddr(item.get().getReceiver());
                    TradeBizResult<ReceiveAddr> result = orderReceiverAbility.checkOnConfirmOrder(req.getCustId(), addr, order);
                    if (result.isSuccess()) {
                        mainOrder.setReceiver(result.getData());
                    } else {
                        inbound.setError(result.getFail());
                    }
                    if(null == addr){
                        ReceiveAddr temp = new ReceiveAddr();
                        temp.setCityCode(item.get().getCityCode());
                        mainOrder.setReceiver(temp);
                    }
                }
            }
        }
        /*ReceiveAddr addr = tcOrderConverter.toReceiveAddr(req.getReceiver());
        TradeBizResult<ReceiveAddr> result = orderReceiverAbility.checkOnConfirmOrder(req.getCustId(), addr, order);
        if (result.isSuccess()) {
            order.setReceiver(result.getData());
        } else {
            inbound.setError(result.getFail());
        }*/
    }
}
