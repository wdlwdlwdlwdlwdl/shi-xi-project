package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;

public interface OrderMessageExt extends IExtensionPoints {

    @AbilityExtension(
        code = "ORDER_CHANGE_MESSAGE",
        name = "订单消息发送",
        description = "订单消息发送"
    )
    void orderMessageSend(OrderChangeNotify change);


    @AbilityExtension(
        code = "ORDER_CHANGE_MESSAGE_MQ",
        name = "订单MQ消息发送",
        description = "订单消息发送"
    )
    void orderMqMessageSend(OrderChangeNotify change);

}
