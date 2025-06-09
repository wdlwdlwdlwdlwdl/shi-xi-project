package com.aliyun.gts.gmall.center.trade.persistence.cartAdd;

import com.aliyun.gts.gmall.center.trade.domain.ext.cartAdd.TcCartRepositoryExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCartMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.repository.impl.TcCartRepositoryImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class TcCartRepositoryImplExt extends TcCartRepositoryImpl implements TcCartRepositoryExt {

    @Autowired
    private TcCartMapper tcCartMapper;

    public TcCartDO queryByUk(CartItemUk cartItemUk) {
        Integer cartType = cartItemUk.getCartType();
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        TcCartDO tcCartDO = tcCartMapper.selectOne(
            tcCartDOLambdaQueryWrapper
            .eq(TcCartDO::getCartType, cartType)
            .eq(TcCartDO::getCustId, cartItemUk.getCustId())
            .eq(TcCartDO::getPayMode, cartItemUk.getPayMode())
            .eq(TcCartDO::getSellerId, cartItemUk.getSellerId())
            .eq(TcCartDO::getSkuId, cartItemUk.getItemSkuId().getSkuId())
            .eq(TcCartDO::getItemId, cartItemUk.getItemSkuId().getItemId())
            .eq(TcCartDO::getSkuQuoteId, cartItemUk.getItemSkuId().getSkuQuoteId()));
        return tcCartDO;
    }

    public List<TcCartDO> queryBatch(CartItemUkBatch ukBatch) {
        Integer cartType = ukBatch.getCartType();
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        Long custId = ukBatch.getCustId();
        List<ItemSkuId> itemSkuId = ukBatch.getItemSkuIds();
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        tcCartDOLambdaQueryWrapper.eq(TcCartDO::getCustId, custId)
            .eq(TcCartDO::getCartType, cartType)
            .eq(TcCartDO::getPayMode, ukBatch.getPayMode())
            .and(inner -> itemSkuId.stream().forEach(id -> {
                inner.or()
                    .nested(i -> i.eq(TcCartDO::getItemId, id.getItemId())
                    .eq(TcCartDO::getSkuId, id.getSkuId())
                    .eq(TcCartDO::getSellerId, id.getSellerId()));
                }
            ));
        List<TcCartDO> list = tcCartMapper.selectList(tcCartDOLambdaQueryWrapper);
        return list;
    }

}
