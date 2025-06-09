package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OrderBizRollbackExt extends IExtensionPoints {

    // 下单失败时, 释放扩展的资源
    void rollbackBizResource(CreatingOrder order);
}
