package com.aliyun.gts.gmall.platform.trade.server.converter;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartSingleQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.DeleteCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.CartPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.SellerPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.ItemDeliveryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemDelivery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.PromotionOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CartRpcConverter {

    @Mappings({
        @Mapping(target = "itemId", source = "itemId"),
        @Mapping(target = "skuId", source = "skuId"),
        @Mapping(target = "quantity", source = "itemQty"),
        @Mapping(target = "custId", source = "custId"),
        @Mapping(target = "cartType", source = "cartType"),
    })
    CartItem toCartItem(AddCartRpcReq req);

    CartItemUkBatch toCartItemUkBatch(DeleteCartRpcReq req);

    CartDTO toCartDTO(Cart cart);

    @Mappings({
        @Mapping(target = "items", source = "cartItems"),
    })
    CartGroupDTO toCartGroupDTO(CartGroup group);

    /**
     * 注意：这个mapping配置没有生效，
     * 因为其实现被trade-center中的CartRpcConverterImplExt类覆盖
     */
    @Mappings({
        @Mapping(target = "itemId", source = "itemId"),
        @Mapping(target = "skuId", source = "skuId"),
        @Mapping(target = "skuName", source = "itemSku.skuName"),
        @Mapping(target = "priceChangeStatus", source = "priceChangeStatus"),
        @Mapping(target = "skuChangeStatus", source = "skuChangeStatus"),
        @Mapping(target = "sellerChangeStatus", source = "sellerChangeStatus"),
        @Mapping(target = "itemChangeStatus", source = "itemChangeStatus"),
        @Mapping(target = "installChangeStatus", source = "installChangeStatus"),
        @Mapping(target = "cartTotalPrice", source = "cartTotalPrice"),
        @Mapping(target = "cartPromotionPrice", source = "cartPromotionPrice"),
        @Mapping(target = "desc", source = "itemSku.skuDesc"),
        @Mapping(target = "skuQty", source = "itemSku.skuQty"),
        @Mapping(target = "cartQty", source = "quantity"),
        @Mapping(target = "originPrice", source = "itemSku.itemPrice.originPrice"),
        @Mapping(target = "itemPrice", source = "itemSku.itemPrice.itemPrice"),
        @Mapping(target = "itemUnitPrice", source = "itemUnitPrice"),
        @Mapping(target = "itemTitle", source = "itemSku.itemTitle"),
        @Mapping(target = "itemPic", source = "itemSku.itemPic"),
        @Mapping(target = "skuPic", source = "itemSku.skuPic"),
        @Mapping(target = "itemStatus", source = "itemSku.itemStatus"),
        @Mapping(target = "itemNotFound", source = "itemNotFound"),
        @Mapping(target = "itemDivideDetails", source = "itemDivideDetails"),
        @Mapping(target = "loan", source = "itemSku.loan"),
        @Mapping(target = "installment", source = "itemSku.installment"),
        @Mapping(target = "sellerId", source = "itemSku.seller.sellerId"),
        @Mapping(target = "sellerName", source = "itemSku.seller.sellerName"),
        @Mapping(target = "sellerStatus", source = "itemSku.seller.sellerStatus"),
        @Mapping(target = "supportDeliveryList", source = "itemSku.supportDeliveryList"),
        @Mapping(target = "selfTimeliness", source = "itemSku.selfTimeliness"),
        @Mapping(target = "itemDelivery", source = "itemSku.itemDelivery"),
    })
    CartItemDTO toCartItemDTO(CartItem item);


    // ========= 价格计算结果 ==============

    @Mappings({
        @Mapping(target = "options", source = "promotions.options"),
        @Mapping(target = "promotionExtend", source = "promotions.promotionExtend"),
        @Mapping(target = "promotionPrice", source = "promotions.promotionPrice"),
        @Mapping(target = "sellers", source = "promotions.sellers"),
    })
    CartPriceDTO toCartPriceDTO(Cart cart);

    @Mappings({
        @Mapping(target = "options", source = "options"),
        @Mapping(target = "promotionExtend", source = "promotionExtend"),
        @Mapping(target = "promotionPrice", source = "promotionPrice"),
        @Mapping(target = "items", source = "items"),
    })
    SellerPriceDTO toSellerPriceDTO(SellerPromotion promotion);

    @Mappings({
        @Mapping(target = "itemId", source = "itemSkuId.itemId"),
        @Mapping(target = "skuId", source = "itemSkuId.skuId"),
        @Mapping(target = "skuQty", source = "skuQty"),
        @Mapping(target = "itemPrice", source = "itemPrice"),
        @Mapping(target = "itemPriceName", source = "itemPriceName"),
        @Mapping(target = "promotionPrice", source = "promotionPrice"),
        @Mapping(target = "itemDivideDetails", source = "itemDivideDetails"),
    })
    ItemPriceDTO toItemPriceDTO(ItemPromotion promotion);

    @Mappings({
        @Mapping(target = "isCoupon", source = "coupon"),
    })
    PromotionOptionDTO toPromotionOptionDTO(PromotionOption opt);

    // ========= 入参 ==============

    @Mappings({
        @Mapping(target = "itemSkuId.itemId", source = "itemId"),
        @Mapping(target = "itemSkuId.skuId", source = "skuId"),
        @Mapping(target = "itemSkuId.sellerId", source = "sellerId"),
    })
    CartItemUk toCartItemUk(CartSingleQueryRpcReq req);


    // 商品物流信息对象
    ItemDeliveryDTO toItemDeliveryDTO(ItemDelivery itemDelivery);

}
