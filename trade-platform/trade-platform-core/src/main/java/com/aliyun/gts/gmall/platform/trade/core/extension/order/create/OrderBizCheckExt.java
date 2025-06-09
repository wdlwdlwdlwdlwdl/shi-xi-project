package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 下单业务检查
 */
public interface OrderBizCheckExt extends IExtensionPoints {

    /**
     * 下单校验
     * @param order
     * @return
     */
    TradeBizResult checkOnConfirmOrder(CreatingOrder order);

    /**
     * 下单业务校验
     * @param order
     * @return
     */
    TradeBizResult checkOnCreateOrder(CreatingOrder order);
}
