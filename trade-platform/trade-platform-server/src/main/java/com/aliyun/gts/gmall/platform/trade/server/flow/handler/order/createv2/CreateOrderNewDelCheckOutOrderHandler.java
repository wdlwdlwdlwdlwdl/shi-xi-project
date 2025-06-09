package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.CacheConstants;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderSaveAbility;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 删除结算时候生成的临时订单
 *    临时表的数据不删除，保留不用
 *    发送消息 删除ES里面存的临时单信息
 * 2025-1-3 16:24:47
 */
@Slf4j
@Component
public class CreateOrderNewDelCheckOutOrderHandler extends TradeFlowHandler.AdapterHandler<TOrderCreate> {

    // 缓存
    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private OrderSaveAbility orderSaveAbility;


    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        //从redis 里面获取
        List<Long> mainIds = cacheRepository.get(String.format(CacheConstants.CREATE_ORINGIN_ORDER_NO, createOrderRpcReq.getConfirmOrderToken()));
        if (Objects.nonNull(mainIds)) {
            // 根据主单号 删除订单
            for (Long primaryOrderId : mainIds) {
                // 发送数据消息处理ES数据
                TcOrderDO orderDO = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
                if(Objects.isNull(orderDO))
                {
                    log.warn("orderDO is null,primaryOrderId:{}",primaryOrderId);
                    continue;
                }
                orderSaveAbility.deleteOrderMessage(orderDO);
                // 数据库删除数据
                tcOrderRepository.deleteCheckOrder(primaryOrderId);


            }
        }
    }
}
