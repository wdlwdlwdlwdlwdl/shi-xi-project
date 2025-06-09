package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;

public interface OrderAutoCancelMsgExt extends IExtensionPoints {

    @AbilityExtension(
        code = "CANCEL_CHANGE_MESSAGE",
        name = "订单超时自动取消消息发送",
        description = "订单消息发送"
    )
    void autoCancelSend(OrderChangeNotify change);

}
