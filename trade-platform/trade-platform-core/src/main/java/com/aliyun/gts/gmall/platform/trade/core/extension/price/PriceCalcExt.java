package com.aliyun.gts.gmall.platform.trade.core.extension.price;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;

import java.util.List;

/**
 * 价格计算扩展点
 */
public interface PriceCalcExt extends IExtensionPoints {

    /**
     * 下单营销查询
     */
    OrderPromotion queryOrderPromotions(CreatingOrder order, QueryFrom from);

    /**
     * 下单营销查询 -- 新版本
     */
    OrderPromotion queryOrderPromotionsNew(CreatingOrder order, QueryFrom from);

    /**
     * 下单营销信息处理完成后的钩子
     */
    void afterOrderPromotions(CreatingOrder order, QueryFrom from);

    /**
     * 购物车营销查询 (选中价格计算)
     */
    OrderPromotion queryCartPromotions(Cart cart);

    /**
     * 购物车营销查询  新版本(选中价格计算)
     * @param cart
     * 2025年2月13日09:51:05
     */
    OrderPromotion queryCartPromotionsNew(Cart cart);

    /**
     * 购物车单品营销查询 (未选中状态)
     */
    List<ItemPromotion> queryCartItemPromotions(Cart cart);

    /**
     * 下单价格计算（营销之后的，包括运费、积分、IC原价 的汇总、分摊等）
     */
    void calcOrderPrices(CreatingOrder order, QueryFrom from);

    /**
     * 下单价格计算（营销之后的，包括运费、积分、IC原价 的汇总、分摊等）
     */
    void calcOrderPriceNew(CreatingOrder order, QueryFrom from);

    /**
     * 下单价格计算（营销之后的，包括运费、积分、IC原价 的汇总、分摊等）
     */
    void calcOrderPriceV2New(CreatingOrder order, QueryFrom from);

}
