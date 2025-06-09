package com.aliyun.gts.gmall.platform.trade.core.extension.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

import java.util.Map;

/**
 * 订单保存扩展点
 * @param <T>
 */
public interface OrderSaveExt<T> extends IExtensionPoints {

    /**
     * 保存前 扩展点
     * @param order
     * @return
     */
    Map beforeSaveOrder(CreatingOrder order);

    /**
     * 订单确认数据对象转换
     * @param order
     * @return
     */
    void convertOrderForConfirm(MainOrder main, CreatingOrder order, Map context);

    /**
     * 下单数据对象转换
     * @param order
     * @return
     */
    void convertOrder(MainOrder main, CreatingOrder order, Map context);

    /**
     * 下单
     * @param context
     * @return
     */
    void saveOrder(Map context);

    /**
     * 下单后订单信息推送ES
     * @param creatingOrder
     * @return
     */
    void pushOrderMessage(CreatingOrder creatingOrder);

    /**
     * 删除订单信息推送ES
     * @param primaryOrderId
     * @return
     */
    void deleteOrderMessage(Long primaryOrderId);
}
