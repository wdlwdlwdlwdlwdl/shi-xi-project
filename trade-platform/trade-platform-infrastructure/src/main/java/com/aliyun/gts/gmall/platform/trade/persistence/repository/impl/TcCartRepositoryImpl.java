package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcCartRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCartMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TcCartRepositoryImpl implements TcCartRepository {

    @Autowired
    private TcCartMapper tcCartMapper;

    @Override
    public List<TcCartDO> queryAll(Long custId, Integer cartType) {
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcCartMapper.selectList(
            tcCartDOLambdaQueryWrapper
            .eq(TcCartDO::getCustId, custId)
            .eq(TcCartDO::getCartType, cartType));
    }

    /**
     * 全量查询
     * @param custId
     * @param cartType
     * @return
     */
    @Override
    public List<TcCartDO> queryPayMode(Long custId, Integer cartType, String payMode) {
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper  = Wrappers.lambdaQuery();
        return tcCartMapper.selectList(
            tcCartDOLambdaQueryWrapper
            .eq(TcCartDO::getCustId, custId)
            .eq(TcCartDO::getPayMode, payMode)
            .eq(TcCartDO::getCartType, cartType)
        );
    }

    /**
     * 用户购物车卡数量总和
     * @param custId
     * @param cartType
     * @return int
     */
    @Override
    public int queryCount(Long custId, Integer cartType) {
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        Long result = tcCartMapper.selectCount(
            tcCartDOLambdaQueryWrapper
            .eq(TcCartDO::getCustId, custId)
            .eq(TcCartDO::getCartType, cartType)
        );
        return result.intValue();
    }

    /**
     * 单商品查询 -- 是否已经加车
     * @param custId
     * @param cartType
     * @param itemSkuId
     * @return TcCartDO
     */
    @Override
    public TcCartDO queryByUk(Long custId, Integer cartType, ItemSkuId itemSkuId) {
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }
        LambdaQueryWrapper<TcCartDO> tcCartDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcCartMapper.selectOne(
            tcCartDOLambdaQueryWrapper
            .eq(TcCartDO::getCustId, custId)
            .eq(TcCartDO::getCartType, cartType)
            .eq(TcCartDO::getItemId, itemSkuId.getItemId())
            .eq(TcCartDO::getSellerId, itemSkuId.getSellerId())
            .eq(TcCartDO::getSkuId, itemSkuId.getSkuId())
        );
    }

    @Override
    public List<TcCartDO> queryBatch(Long custId, Integer cartType, List<ItemSkuId> itemSkuId) {
        if (cartType == null) {
            cartType = TcCartDO.CART_TYPE_DEFAULT;
        }

        LambdaQueryWrapper<TcCartDO> q = Wrappers.lambdaQuery();
        q.eq(TcCartDO::getCustId, custId).eq(TcCartDO::getCartType, cartType)
            // and ( (item_id = xx and sku_id = xx) or () or () or ... )
            .and(inner -> itemSkuId.stream().forEach(id -> {
                inner.or().nested(i -> i.eq(TcCartDO::getItemId, id.getItemId()).eq(TcCartDO::getSkuId, id.getSkuId()));
            }));
        return tcCartMapper.selectList(q);
    }

    @Override
    public List<TcCartDO> queryBatch(Long custId, Long sellerId, String payMode, List<ItemSkuId> itemSkuId) {
        if(null == custId || null == sellerId || null == payMode) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TcCartDO> q = Wrappers.lambdaQuery();
        q.eq(TcCartDO::getCustId, custId).eq(TcCartDO::getSellerId, sellerId).eq(TcCartDO::getPayMode, payMode)
            // and ( (item_id = xx and sku_id = xx) or () or () or ... )
            .and(inner -> itemSkuId.forEach(id -> {
                inner.or().nested(i -> i.eq(TcCartDO::getItemId, id.getItemId()).eq(TcCartDO::getSkuId, id.getSkuId()));
            }));
        return tcCartMapper.selectList(q);
    }

    @Override
    public void create(TcCartDO cart) {
        if (cart.getCartType() == null) {
            cart.setCartType(TcCartDO.CART_TYPE_DEFAULT);
        }
        tcCartMapper.insert(cart);
    }

    @Override
    public void updateById(TcCartDO cart) {
        tcCartMapper.updateById(cart);
    }

    @Override
    public void deleteById(Long id) {
        tcCartMapper.deleteById(id);
    }

    /**
     * 批量删除
     * @param deleteCartIds
     */
    @Override
    public void deleteByIds(List<Long> deleteCartIds) {
        tcCartMapper.deleteByIds(deleteCartIds);
    }
}
