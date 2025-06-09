package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;

import java.util.List;

public interface CartService {

    /**
     * 购物车商品查询
     */
    List<CartItem> queryAll(Long custId, Integer cartType);

    /**
     * 购物车商品查询 -- 通过支付方式
     */
    List<CartItem> queryPayMode(Long custId, Integer cartType, String payMode);

    /**
     * 购物车商品数量
     */
    int queryCount(Long custId, Integer cartType);

    /**
     * 单用户批量查询
     */
    List<CartItem> queryItems(CartItemUkBatch ukBatch);

    /**
     * 单用户批量操作,halyk
     * @param ukBatch
     * @return
     */
    List<CartItem> queryCartItems(CartItemUkBatch ukBatch);

    /**
     * 单商品查询
     */
    CartItem queryItem(CartItemUk uk);

    /**
     * 按时间排序、按卖家分组
     */
    void sortGrouping(Cart cart);

    /**
     * 保存DB, 有ID时update, 无ID时insert
     */
    void save(CartItem cartItem);

    /**
     * 保存DB, 同时删除指定ID的记录（用于合并数据）
     */
    void save(CartItem cartItem, Long deleteId);

    /**
     * 单用户批量删除
     */
    void deleteItems(CartItemUkBatch ukBatch);

    /**
     * 单用户批量删除
     */
    void deleteItems(List<Long> deleteCartIds);

    /**
     * 清空购物车
     */
    void clearItems(Long custId, Integer cartType);
}
