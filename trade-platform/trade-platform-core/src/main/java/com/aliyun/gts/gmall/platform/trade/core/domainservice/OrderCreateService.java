package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SplitOrderItemInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

import java.util.Collection;
import java.util.List;

public interface OrderCreateService {

    /**
     * 拆单
     */
    List<MainOrder> splitOrder(Collection<ItemSku> itemList);

    /**
     * 根据商品进行拆单
     * @param orderItems
     * @return   List<MainOrder>
     * 2024-12-13 15:14:58
     */
    List<MainOrder> splitOrderNew(List<SplitOrderItemInfo> orderItems);

    /**
     * 拆单
     */
    List<MainOrder> splitOrder(List<MainOrder> mainOrders);

    /**
     * 生成用于创建订单的token参数, 包含商品、收件人、优惠选择、价格计算、幂等ID生成
     */
    String getOrderToken(CreatingOrder order, String token);

    /**
     * token信息拆包, 同时对幂等ID加锁
     */
    CreatingOrder unpackToken(String token);

    /**
     * token信息拆包, 同时对幂等ID加锁
     */
    CreatingOrder unpackTokenCache(String token);

    /**
     * 回收token, available=true 继续有效, available=false 失效掉token
     */
    void recoverToken(CreatingOrder order, boolean available);

    /**
     * 保存订单、支付单，同时创建超时关单任务
     */
    void saveOrder(CreatingOrder order);

    /**
     *
     * @param order
     */
    void saveOrderForConfirm(CreatingOrder order);


}
