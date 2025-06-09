package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.PromotionOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import org.mapstruct.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PromotionConverter {

    // ====================== cart -> query =============================

    @Mappings({
        @Mapping(target = "sellers", source = "groups"),
        @Mapping(target = "orderChannel", source = "channel"),
        @Mapping(target = "promotionSource", source = "promotionSource")
    })
    OrderPromotion cartToPromotionQuery(Cart cart);

    @Mappings({
        @Mapping(target = "sellers", source = "groupsBySeller"),
        @Mapping(target = "orderChannel", source = "channel"),
        @Mapping(target = "promotionSource", source = "promotionSource")
    })
    OrderPromotion cartToPromotionQueryBySeller(Cart cart);

    @Mappings({
        @Mapping(target = "sellerId", source = "sellerId"),
        @Mapping(target = "items", source = "cartItems", qualifiedByName = "toItems"),
    })
    SellerPromotion cartToPromotionQuery(CartGroup group);

    @Named("toItems")
    default List<ItemPromotion> toItems(List<CartItem> cartItems) {
        return cartItems.stream()
            .filter(item -> item.getItemSku() != null)
            .map(item -> cartToPromotionQuery(item))
            .collect(Collectors.toList());
    }

    @Mappings({
        @Mapping(target = "itemSkuId.itemId", source = "itemId"),
        @Mapping(target = "itemSkuId.skuId", source = "skuId"),
        @Mapping(target = "itemSkuId.sellerId", source = "sellerId"),
        @Mapping(target = "skuQty", source = "quantity"),
        @Mapping(target = "originPrice", source = "itemSku.itemPrice.originPrice"),
        @Mapping(target = "itemSku", source = "itemSku"),
    })
    ItemPromotion cartToPromotionQuery(CartItem item);

    // ====================== option dto --> domain =============================

    PromotionOption toOrderPromotion(PromotionOptionDTO option);

    List<PromotionOption> toOrderPromotions(Collection<PromotionOptionDTO> options);


    // ====================== order -> query =============================

    @Mappings({
        @Mapping(target = "sellers", source = "mainOrders"),
        @Mapping(target = "options", source = "promotions.options"),
        @Mapping(target = "promotionExtend", source = "promotions.promotionExtend"),
        @Mapping(target = "custId", source = "customer.custId"),
        @Mapping(target = "orderChannel", source = "orderChannel"),
        @Mapping(target = "promotionSource", source = "promotionSource"),
        @Mapping(target = "extraMap", source = "params"),
    })
    OrderPromotion orderToPromotionQuery(CreatingOrder order);

    @Mappings({
        @Mapping(target = "sellerId", source = "seller.sellerId"),
        @Mapping(target = "items", source = "subOrders"),
        @Mapping(target = "options", source = "promotions.options"),
        @Mapping(target = "promotionExtend", source = "promotions.promotionExtend"),
    })
    SellerPromotion orderToPromotionQuery(MainOrder main);

    @Mappings({
        @Mapping(target = "itemSkuId.itemId", source = "itemSku.itemId"),
        @Mapping(target = "itemSkuId.skuId", source = "itemSku.skuId"),
        @Mapping(target = "skuQty", source = "orderQty"),
        @Mapping(target = "originPrice", source = "itemSku.itemPrice.originPrice"),
        @Mapping(target = "itemSku", source = "itemSku"),
    })
    ItemPromotion orderToPromotionQuery(SubOrder sub);


    // ====================== promotion -> order =============================

    @Mappings({
        @Mapping(target = "promotions", source = "pro"),
        @Mapping(target = "orderPrice.orderPromotionAmt", source = "pro.promotionPrice"),
        @Mapping(target = "mainOrders", expression = "java(promotionToOrderMain(pro.getSellers(), order.getMainOrders()))"),
        @Mapping(target = "extraMap", ignore = true),
        @Mapping(target = "custId", ignore = true),
    })
    void promotionToOrder(OrderPromotion pro, @MappingTarget CreatingOrder order);

    @Mappings({
        @Mapping(target = "promotions", source = "pro"),
        @Mapping(target = "orderPrice.orderPromotionAmt", source = "pro.promotionPrice"),
        @Mapping(target = "subOrders", expression = "java(promotionToOrderSub(pro.getItems(), main.getSubOrders()))"),
        @Mapping(target = "extraMap", ignore = true),
    })
    void promotionToOrder(SellerPromotion pro, @MappingTarget MainOrder main);

    @Mappings({
        @Mapping(target = "promotions", source = "pro"),
        @Mapping(target = "orderPrice.orderPromotionAmt", source = "pro.promotionPrice"),
        @Mapping(target = "itemSku.itemPrice.itemPrice", source = "pro.itemPrice"),
        @Mapping(target = "extraMap", expression = "java(mergeExtMap(pro.getExtraMap(), sub.getExtraMap()))"),
    })
    void promotionToOrder(ItemPromotion pro, @MappingTarget SubOrder sub);

    default Map<String, Object> mergeExtMap(Map<String, Object> source, Map<String, Object> target) {
        if (source == null) {
            return target;
        }
        if (target == null) {
            target = new HashMap<>();
        }
        target.putAll(source);
        return target;
    }

    @Named("promotionToOrderMain")
    default List<MainOrder> promotionToOrderMain(List<SellerPromotion> sellers, List<MainOrder> list) {
        Map<Long, SellerPromotion> slrMap = CommUtils.toMap(sellers, SellerPromotion::getSellerId);
        for (MainOrder main : list) {
            SellerPromotion p = slrMap.get(main.getSeller().getSellerId());
            promotionToOrder(p, main);
        }
        return list;
    }

    @Named("promotionToOrderSub")
    default List<SubOrder> promotionToOrderSub(List<ItemPromotion> items, List<SubOrder> list) {
        Map<ItemSkuId, ItemPromotion> itemMap = CommUtils.toMap(items, ItemPromotion::getItemSkuId);
        for (SubOrder sub : list) {
            ItemPromotion p = itemMap.get(sub.getItemSku().getItemSkuId());
            promotionToOrder(p, sub);
            sub.getOrderPrice().setItemPrice(sub.getItemSku().getItemPrice());
        }
        return list;
    }

     @Named("convertMainPromotion")
     default void convertMainPromotion(CreatingOrder order) {
         order.getMainOrders().forEach(mainOrder -> {
             SellerPromotion sellerPromotion = new SellerPromotion();
             if (Objects.nonNull(mainOrder.getPromotions())) {
                 sellerPromotion.setSellerId(mainOrder.getPromotions().getSellerId());
             }
             OrderPrice orderPrice = new OrderPrice();
             mainOrder.setOrderPrice(orderPrice);
             mainOrder.setPromotions(sellerPromotion);
             AtomicReference<Long> promotionPrice = new AtomicReference<>(0L);
             List<ItemPromotion> items = new ArrayList<>();

             mainOrder.getSubOrders().forEach(subOrder ->
             {
                 ItemPromotion promotion=  subOrder.getPromotions();
                 if(Objects.nonNull(promotion) && Objects.nonNull(promotion.getPromotionPrice()))
                 {
                     promotionPrice.updateAndGet(v -> v + promotion.getPromotionPrice());
                     items.add(promotion);
                 }
             });
             mainOrder.getPromotions().setPromotionPrice(promotionPrice.get());
             mainOrder.getPromotions().setItems(items);
             orderPrice.setOrderPromotionAmt(promotionPrice.get());
         });
     }
}
