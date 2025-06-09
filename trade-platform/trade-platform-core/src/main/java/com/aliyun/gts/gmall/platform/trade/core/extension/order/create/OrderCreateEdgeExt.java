package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OrderCreateEdgeExt extends IExtensionPoints {

    /**
     * 开始下单流程, 仅完成了 unpack token
     */
    void beginCreate(CreatingOrder order);

    /**
     * 保存数据库成功
     */
    void orderSaved(CreatingOrder order);

    /**
     * 保存数据库之前失败
     */
    void failedWithoutSave(CreatingOrder order);
}
