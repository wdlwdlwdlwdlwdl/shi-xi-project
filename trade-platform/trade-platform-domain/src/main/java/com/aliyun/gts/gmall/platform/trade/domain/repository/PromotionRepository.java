package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotionQuery;

import java.util.List;

public interface PromotionRepository {

    /**
     * 查单品折扣信息
     */
    List<ItemPromotion> queryItemPromotion(ItemPromotionQuery query);


    /**
     * 查合并下单、购物车组合优惠信息
     */
    OrderPromotion queryOrderPromotion(OrderPromotion query, QueryFrom from);

    /**
     * 查合并下单、购物车组合优惠信息
     * @param query
     * @param from
     * 2025-2-13 09:52:16
     */
    OrderPromotion queryOrderPromotionNew(OrderPromotion query, QueryFrom from);

    /**
     * 查优惠来源参数
     * CART-购物车查询优惠，CONFIRM_ORDER-确认订单查询优惠，CREATE_ORDER-创建订单查询优惠
     */
    enum QueryFrom {
        CART, CONFIRM_ORDER, CREATE_ORDER
    }

    /**
     * 扣除营销资产（优惠券）
     */
    void deductUserAssets(CreatingOrder order);

    /**
     * 回滚营销资产（优惠券）
     */
    void rollbackUserAssets(CreatingOrder order);

    /**
     * 订单退券 逆向流程退券
     * 回滚营销资产（优惠券）
     */
    void orderRollbackUserAssets(MainOrder mainOrder);

}
