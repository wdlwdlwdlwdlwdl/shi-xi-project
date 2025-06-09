package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 满赠 -- 赠商品服务
 */
public interface ManzengGiftService {

    /**
     * 下单处理钩子, 营销查询之后, 如有赠品则添加赠品
     */
    void onOrderCreate(CreatingOrder order);

    /**
     * 下单处理, 临时移除赠品订单
     */
    void withoutGiftOrder(CreatingOrder order, Consumer<CreatingOrder> withoutGiftOrder);

    /**
     * 下单处理, 判断赠品订单
     */
    boolean isGiftOrder(CreatingOrder order, SubOrder sub);

    /**
     * 下单处理钩子, 保存db前
     */
    void beforeSave(CreatingOrder order);

    /**
     * 主商品支付完成, 推进赠品订单
     */
    void onOrderPaid(MainOrder mainOrder);

    /**
     * 未付款取消订单 (含买家/卖家/系统 取消)
     */
    void onOrderCancel(OrderMessageDTO message);

}
