package com.aliyun.gts.gmall.platform.trade.core.ability.price;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.price.DefaultPriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 交易订单 下单计算扩展点
 */
@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility",
    fallback = DefaultPriceCalcExt.class,
    description = "价格计算扩展能力"
)
public class PriceCalcAbility extends BaseAbility<BizCodeEntity, PriceCalcExt> {

    /**
     * 查询营销计算
     * @param order
     * @param from
     * @return
     */
    public OrderPromotion queryOrderPromotions(CreatingOrder order, QueryFrom from) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            return this.executeExt(
                bizCode,
                extension -> extension.queryOrderPromotions(order, from),
                PriceCalcExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 查询营销计算
     * @param order
     * @param from
     * @return
     */
    public OrderPromotion queryOrderPromotionsNew(CreatingOrder order, QueryFrom from) {
        // 取第一个bizCode             取下单时所有订单的bizCode 集合，去重
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) { //直接调用BizCodeEntity类的静态函数获取订单的bizCode
            return this.executeExt(
                bizCode,
                extension -> extension.queryOrderPromotionsNew(order, from),
                PriceCalcExt.class, // 指定扩展实现类，表示当前操作需要使用的扩展类需要实现PriceCalcExt接口
                Reducers.firstOf(Objects::nonNull)  //返回第一个非空值
            );
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 营销计算后的扩展点
     * @param order
     * @param from
     */
    public void afterOrderPromotions(CreatingOrder order, QueryFrom from) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            this.executeExt(
                bizCode,
                extension -> {
                    // 下单营销后的钩子
                    extension.afterOrderPromotions(order, from);
                    return null;
                },
                PriceCalcExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
            return;
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 交易查询营销方案 -- 旧
     * @param cart
     * @return
     */
    public OrderPromotion queryCartPromotions(Cart cart) {
        // 取线程bizCode
        return this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.queryCartPromotions(cart),
            PriceCalcExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }


    /**
     * 交易查询营销方案 -- 旧
     * @param cart
     * @return
     */
    public OrderPromotion queryCartPromotionsNew(Cart cart) {
        // 取线程bizCode
        return this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.queryCartPromotionsNew(cart),
            PriceCalcExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 查询营销接口
     * @param cartItem
     * @param channel
     * @return
     */
    public OrderPromotion queryCartPromotions(CartItem cartItem, String channel) {
        Cart cart = new Cart();
        CartGroup cartGroup = new CartGroup();
        cartGroup.setCartItems(List.of(cartItem));
        cart.setChannel(channel);
        cart.setGroups(List.of(cartGroup));
        cart.setCustId(cartItem.getCustId());
        cartGroup.setSellerId(cartItem.getSellerId());
        cart.setGroupsBySeller(List.of(cartGroup));
        // 取线程bizCode
        return this.executeExt(BizCodeEntity.getFromThreadLocal(),
            extension -> extension.queryCartPromotions(cart),
            PriceCalcExt.class, Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 购物车营销价格计算 --- 标品能力
     * @param cart
     * @return
     */
    public List<ItemPromotion> queryCartItemPromotions(Cart cart) {
        // 取线程bizCode
        return this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.queryCartItemPromotions(cart),
            PriceCalcExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 计算交易订单商品价格，通过扩展点计算
     * @param order
     * @param from
     */
    public void calcOrderPrices(CreatingOrder order, QueryFrom from) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            this.executeExt(
                bizCode,
                extension -> {
                    /**
                     * 基础实现类 DefaultPriceCalcExt
                     * 扩展实现类 CommonPriceCalcExt
                     */
                    extension.calcOrderPrices(order, from);
                    return null;
                },
                PriceCalcExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
            return;
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 计算交易订单商品价格，通过扩展点计算
     * @param order
     * @param from
     */
    public void calcOrderPriceNew(CreatingOrder order, QueryFrom from) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            this.executeExt(
                bizCode,
                extension -> {
                    extension.calcOrderPriceNew(order, from);
                    return null;
                },
                PriceCalcExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
            return;
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }

}
