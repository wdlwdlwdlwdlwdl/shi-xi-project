package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

import java.util.List;

public interface OrderPushAbility {

    /**
     * 发送消息
     */
    void send(List<TcOrderDO> orderList,Integer type);

    /**
     * 发消息
     * @param mainOrder
     * @param type
     */
    void send(MainOrder mainOrder, Integer type);

    /**
     * 发消息--- 订单取消场景 超时push
     * @param orderList
     */
    void sendOrderCancel(List<TcOrderDO> orderList,Integer primaryOrderStatus);

    /**
     * 取消发送邮箱 push
     * @param mainOrder
     */
    void sendOrderCancel(MainOrder mainOrder);

}
