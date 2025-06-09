package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 满赠 -- 优惠券、积分等发放
 */
public interface ManzengGrantService {

    /**
     * 订单完成(确认收货) -- 发放满赠奖品, 幂等
     */
    void onManzengSend(OrderMessageDTO message);

    /**
     * 支付成功处理:商品赠送
     */
    void giftOrderGrantItem(MainOrder mainOrder);

    /**
     * 满赠订单支付
     * @param message
     */
    void onManzengPaid(OrderMessageDTO message);
}
