package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;

import java.util.List;

public interface DeliveryStatusChangeExt extends IExtensionPoints {

    @AbilityExtension(
        code = "DELIVERY_STATUS_CHANGE",
        name = "物流状态变更",
        description = "物流状态变更"
    )
    TradeBizResult<List<TcOrderDO>> orderStatusChange(OrderStatus orderStatus);

}
