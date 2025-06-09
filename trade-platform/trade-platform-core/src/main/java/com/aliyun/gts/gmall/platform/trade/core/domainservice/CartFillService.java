package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;



/**
 * 购物车补全服务
 */
public interface CartFillService {

    /**
     * 商品信息补全，调用商品中心服务、卖家服务
     */
    void fillItemInfo(Cart cart);

    /**
     * 补全单品营销信息
     */
    void fillItemPromotions(Cart cart);

    /**
     * 补全单品营销信息
     * @param cart
     * @return
     */
    OrderPromotion fillCartItemPromotions(Cart cart);

    /**
     * 补全单品营销信息
     * @param cart
     * @return
     */
    void fillCartGroupItemPromotions(Cart cart);

    /**
     * 补全组合下单优惠信息
     */
    void fillOrderPromotions(Cart cart);

}
