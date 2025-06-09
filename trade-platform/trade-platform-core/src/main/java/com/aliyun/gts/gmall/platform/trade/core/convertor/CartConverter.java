
package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.CartFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class CartConverter {

    @Mappings({
        @Mapping(target = "features", source = "cartItem"),
    })
    public abstract TcCartDO toTcCartDO(CartItem cartItem);

    @Mappings({
        @Mapping(target = "itemSku", source = "features"),
        @Mapping(target = "cartId", source = "id"),
    })
    public abstract CartItem toCartItem(TcCartDO cartDO);


    // cart_features 商品部分优先从 itemSKu 获取, 其他保留自定义扩展性
    protected CartFeatureDO getCartFeatures(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        CartFeatureDO feature = new CartFeatureDO();
        toCartFeatures(feature, cartItem.getFeatures());
        toCartFeatures(feature, cartItem.getItemSku());
        return feature;
    }

    protected abstract void toCartFeatures(@MappingTarget CartFeatureDO target, CartFeatureDO cartItem);
    protected abstract void toCartFeatures(@MappingTarget CartFeatureDO target, ItemSku itemSku);
}
