package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;

/**
 * 下单收货地址校验扩展点
 */
public interface OrderReceiverExt extends IExtensionPoints {

    /**
     * 收货地址校验
     * @param custId
     * @param addr
     * @return
     */
    TradeBizResult<ReceiveAddr> checkOnConfirmOrder(Long custId, ReceiveAddr addr);

    /**
     * 收货地址校验
     * @param custId
     * @param addr
     * @return
     */
    TradeBizResult<ReceiveAddr> checkOnCreateOrder(Long custId, ReceiveAddr addr);

    /**
     * 填订单物流方式等信息
     * @param order
     */
    void fillLogisticsInfo(CreatingOrder order);
}
