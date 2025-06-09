package com.aliyun.gts.gmall.center.trade.core.extension.common;


import com.aliyun.gts.gmall.center.trade.core.domainservice.*;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.ItemPriceInput;
import com.aliyun.gts.gmall.platform.trade.common.util.CreatingOrderParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.price.DefaultPriceCalcExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 价格计算 全局的扩展实现
 */
@Slf4j
public class CommonPriceCalcExt extends DefaultPriceCalcExt {

    @Autowired
    private ItemPriceDomainService itemPriceDomainService;
    @Autowired
    private ManzengGiftService manzengGiftService;
//    @Autowired
//    private OrderPriceRiskService orderPriceRiskService;
    @Autowired
    private FixedPointPriceService fixedPointPriceService;
    @Autowired
    private B2bSourcingDomainService b2bSourcingDomainService;

    @Override
    public void calcOrderPrices(CreatingOrder order, QueryFrom from) {
        super.calcOrderPrices(order, from);
        // 价格风控处理
        //orderPriceRiskService.check(order);
    }

    @Override
    public OrderPromotion queryCartPromotions(Cart cart) {
        List<ItemPriceInput> list = new ArrayList<>();
        for (CartGroup group : cart.getGroups()) {
            for (CartItem item : group.getCartItems()) {
                ItemPriceInput input = ItemPriceInput.builder()
                    .itemSku(item.getItemSku())
                    .quantity(item.getQuantity())
                    .custId(cart.getCustId())
                    .build();
                list.add(input);
            }
        }
//        itemPriceDomainService.calcItemPrice(list);
        return super.queryCartPromotions(cart);
    }



    @Override
    public OrderPromotion queryOrderPromotions(CreatingOrder order, QueryFrom from) {
        // 积分兑换订单
//        if (fixedPointPriceService.isFixedOrder(order)) {
//            return fixedPointPriceService.getFixedOrderPromotion(order);
//        }
        // 寻源订单
//        if (b2bSourcingDomainService.isSourcing(order)) {
//            return b2bSourcingDomainService.getSourcingPromotion(order);
//        }
        List<ItemPriceInput> list = new ArrayList<>();
        for (MainOrder main : order.getMainOrders()) {
            for (SubOrder sub : main.getSubOrders()) {
                ItemPriceInput input = ItemPriceInput.builder()
                    .itemSku(sub.getItemSku())
                    .quantity(sub.getOrderQty())
                    .custId(order.getCustomer().getCustId())
                    .build();
                list.add(input);
            }
        }
//        itemPriceDomainService.calcItemPrice(list);
        return super.queryOrderPromotions(order, from);
    }


    /**
     * 营销计算后的钩子方法
     * @anthor shifeng
     * @param order
     * @param from
     * 2024-12-12 09:44:47
     */
    @Override
    public void afterOrderPromotions(CreatingOrder order, QueryFrom from) {
        super.afterOrderPromotions(order, from);
        // 满赠钩子
        if (from == QueryFrom.CREATE_ORDER) {
            manzengGiftService.onOrderCreate(order);
        }
    }

    @Override
    public List<ItemPromotion> queryCartItemPromotions(Cart cart) {
        for (CartGroup group : cart.getGroups()) {
            for (CartItem item : group.getCartItems()) {
                if (item.isItemNotFound() || !item.getItemSku().isEnabled()) {
                    continue;
                }
                ItemPriceInput input = ItemPriceInput.builder()
                    .itemSku(item.getItemSku())
                    .quantity(item.getQuantity())
                    .custId(cart.getCustId())
                    .build();
                itemPriceDomainService.calcItemPrice(input);
            }
        }
        return super.queryCartItemPromotions(cart);
    }

    @Override
    protected void processFreight(CreatingOrder order, QueryFrom from) {
//        if (fixedPointPriceService.isFixedOrder(order)) {
//            // 积分兑换订单
//            fixedPointPriceService.fillFreight(order);
//        } else if (b2bSourcingDomainService.isSourcing(order)) {
//            // 寻源订单
//            b2bSourcingDomainService.fillFreight(order);
//        } else {
//            // 排除赠品, 计算运费
//            manzengGiftService.withoutGiftOrder(order,
//                    withoutGift -> super.processFreight(withoutGift, from));
//        }
        // 排除赠品, 计算运费
        manzengGiftService.withoutGiftOrder(order, withoutGift -> super.processFreight(withoutGift, from));
    }

    @Override
    protected void processPoint(CreatingOrder order) {
//        if (fixedPointPriceService.isFixedOrder(order)) {
//            // 积分兑换订单
//            fixedPointPriceService.fillOrderPoints(order);
//        } else {
//            super.processPoint(order);
//        }
        // 不存在积分订单
        super.processPoint(order);
    }
}
