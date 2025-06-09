package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface OrderSuccessExt extends IExtensionPoints {

    /**
     * 订单售中完成处理, 发送ORDER_SUCCESS 消息之前, 记录确认收货金额、分账金额到DB
     */
    void processOrderSuccess(MainOrder mainOrder);

}
