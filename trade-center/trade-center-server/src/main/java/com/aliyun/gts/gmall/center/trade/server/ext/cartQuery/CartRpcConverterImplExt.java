package com.aliyun.gts.gmall.center.trade.server.ext.cartQuery;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverterImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Primary
@Component
public class CartRpcConverterImplExt extends CartRpcConverterImpl {



    @Override
    public CartItem toCartItem(AddCartRpcReq req) {
        if ( req == null ) {
            return null;
        }

        CartItem cartItem = new CartItem();

        cartItem.setItemId( req.getItemId() );
        cartItem.setSkuId( req.getSkuId() );
        cartItem.setQuantity( req.getItemQty() );
        cartItem.setCustId( req.getCustId() );
        cartItem.setCartType( req.getCartType() );
        cartItem.setSellerId(req.getSellerId());
        cartItem.setPayMode(req.getPayMode());
        cartItem.setSkuQuoteId(req.getSkuQuoteId());
        cartItem.setCityCode(req.getCityCode());

        return cartItem;
    }

    @Override
    public CartGroupDTO toCartGroupDTO(CartGroup group) {
        if ( group == null ) {
            return null;
        }
        CartGroupDTO cartGroupDTO = new CartGroupDTO();
        cartGroupDTO.setItems(cartItemListToCartItemDTOList(group.getCartItems()));
        cartGroupDTO.setGroupType(group.getGroupType());
        cartGroupDTO.setPayMode(group.getPayMode());
        cartGroupDTO.setSellerName(group.getSellerName());
        cartGroupDTO.setSellerId(group.getSellerId());
        cartGroupDTO.setSelectEnable(group.getSelectEnable());
        cartGroupDTO.setGroupTotalPrice(group.getGroupTotalPrice());
        cartGroupDTO.setGroupPromotionPrice(group.getGroupPromotionPrice());
        cartGroupDTO.setGroupItemTotal(group.getGroupItemTotal());
        return cartGroupDTO;
    }



    @Override
    public CartItemDTO toCartItemDTO(CartItem item) {
        if ( item == null ) {
            return null;
        }

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setItemId( item.getItemId() );
        cartItemDTO.setSkuId( item.getSkuId() );
        if (item.getItemSku() != null) {
            cartItemDTO.setSkuName( item.getItemSku().getSkuName() );
        }
        cartItemDTO.setDesc( itemItemSkuSkuDesc( item ) );
        cartItemDTO.setSkuQty( itemItemSkuSkuQty( item ) );
        cartItemDTO.setCartQty( item.getQuantity() );
        cartItemDTO.setOriginPrice( itemItemSkuItemPriceOriginPrice( item ) );
        cartItemDTO.setItemPrice( itemItemSkuItemPriceItemPrice( item ) );
        cartItemDTO.setPriceChangeStatus( item.getPriceChangeStatus() );
        cartItemDTO.setSkuChangeStatus( item.getSkuChangeStatus() );
        cartItemDTO.setSellerChangeStatus( item.getSellerChangeStatus() );
        cartItemDTO.setItemChangeStatus( item.getItemChangeStatus() );
        cartItemDTO.setInstallChangeStatus( item.getInstallChangeStatus() );
        cartItemDTO.setCartTotalPrice( item.getCartTotalPrice() );
        cartItemDTO.setCartPromotionPrice( item.getCartPromotionPrice() );
        cartItemDTO.setItemTitle( itemItemSkuItemTitle( item ) );
        cartItemDTO.setItemPic( itemItemSkuItemPic( item ) );
        cartItemDTO.setSkuPic( itemItemSkuSkuPic( item ) );
        cartItemDTO.setSellerId(item.getSellerId());
        cartItemDTO.setSkuQuoteId( item.getSkuQuoteId() );
        cartItemDTO.setCityCode( item.getCityCode() );
        cartItemDTO.setItemUnitPrice(item.getItemUnitPrice());
        if (item.getItemSku() != null && item.getItemSku().getSeller() != null) {
            cartItemDTO.setSellerStatus(item.getItemSku().getSeller().getSellerStatus());
            cartItemDTO.setSellerName(item.getItemSku().getSeller().getSellerName());
        }
        if (item.getItemSku() != null) {
            cartItemDTO.setLoan( item.getItemSku().getLoan());
            cartItemDTO.setInstallment( item.getItemSku().getInstallment());
        }
        if (item.getItemSku() != null) {
            cartItemDTO.setBrandId(item.getItemSku().getBrandId());
        }
        if (item.getItemSku() != null) {
            cartItemDTO.setCategoryId(item.getItemSku().getCategoryId());
            cartItemDTO.setSupportDeliveryList(item.getItemSku().getSupportDeliveryList());
            cartItemDTO.setSelfTimeliness(item.getItemSku().getSelfTimeliness());
//            cartItemDTO.setItemDelivery(itemDeliveryListToItemDeliveryDTOList(item.getItemSku().getItemDelivery()));
        }
        cartItemDTO.setItemNotFound( item.isItemNotFound() );
        cartItemDTO.setAddCartPrice(item.getAddCartPrice());
        cartItemDTO.setOriginInstallment(item.getOriginInstallment());
        cartItemDTO.setCurrentInstallment(item.getCurrentInstallment());
        cartItemDTO.setSelectEnable(item.getSelectEnable());
        cartItemDTO.setItemDivideDetails( itemDivideDetailListToItemDividePromotionDTOList( item.getItemDivideDetails() ) );
        Map<String, String> extra = new HashMap<>();
        extra.put("sellerId", String.valueOf(item.getSellerId()));
        extra.put("sellerName", String.valueOf(item.getFeatures().getSellerName()));
        extra.put("addOriginPrice", String.valueOf(item.getFeatures().getAddOriginPrice()));
        cartItemDTO.setExtra(extra);
        cartItemDTO.setOriginalPayMode(item.getOriginalPayMode());
        cartItemDTO.setCartId(item.getCartId());
        return cartItemDTO;
    }

    private Integer itemItemSkuItemStatus(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        Integer itemStatus = itemSku.getItemStatus();
        if ( itemStatus == null ) {
            return null;
        }
        return itemStatus;
    }

    private String itemItemSkuSkuPic(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        String skuPic = itemSku.getSkuPic();
        if ( skuPic == null ) {
            return null;
        }
        return skuPic;
    }


    private String itemItemSkuItemPic(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        String itemPic = itemSku.getItemPic();
        if ( itemPic == null ) {
            return null;
        }
        return itemPic;
    }


    private String itemItemSkuItemTitle(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        String itemTitle = itemSku.getItemTitle();
        if ( itemTitle == null ) {
            return null;
        }
        return itemTitle;
    }


    private String itemItemSkuSkuDesc(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        String skuDesc = itemSku.getSkuDesc();
        if ( skuDesc == null ) {
            return null;
        }
        return skuDesc;
    }

    private Integer itemItemSkuSkuQty(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        Integer skuQty = itemSku.getSkuQty();
        if ( skuQty == null ) {
            return null;
        }
        return skuQty;
    }

    private Long itemItemSkuItemPriceOriginPrice(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        ItemPrice itemPrice = itemSku.getItemPrice();
        if ( itemPrice == null ) {
            return null;
        }
        Long originPrice = itemPrice.getOriginPrice();
        if ( originPrice == null ) {
            return null;
        }
        return originPrice;
    }

    private Long itemItemSkuItemPriceItemPrice(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        ItemSku itemSku = cartItem.getItemSku();
        if ( itemSku == null ) {
            return null;
        }
        ItemPrice itemPrice = itemSku.getItemPrice();
        if ( itemPrice == null ) {
            return null;
        }
        Long itemPrice1 = itemPrice.getItemPrice();
        if ( itemPrice1 == null ) {
            return null;
        }
        return itemPrice1;
    }


}
