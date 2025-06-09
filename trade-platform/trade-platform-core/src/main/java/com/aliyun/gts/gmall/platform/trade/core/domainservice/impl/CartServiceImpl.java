package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import cn.hutool.core.collection.CollUtil;
import com.aliyun.gts.gmall.platform.trade.common.constants.CartGroupTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CartConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcCartRepository;
import com.google.common.collect.LinkedHashMultimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TcCartRepository tcCartRepository;
    @Autowired
    private CartConverter cartConverter;

    @Override
    public List<CartItem> queryAll(Long custId, Integer cartType) {
        List<TcCartDO> list = tcCartRepository.queryAll(custId, cartType);
        return list.stream()
            .map(cart -> cartConverter.toCartItem(cart))
            .collect(Collectors.toList());
    }

    /**
     * 购物车商品查询 -- 通过支付方式
     * @param custId
     * @param cartType
     * @param payMode
     * @return
     */
    @Override
    public List<CartItem> queryPayMode(Long custId, Integer cartType, String payMode) {
        List<TcCartDO> list = tcCartRepository.queryPayMode(custId, cartType, payMode);
        return list.stream()
            .map(cart -> cartConverter.toCartItem(cart))
            .collect(Collectors.toList());
    }

    @Override
    public int queryCount(Long custId, Integer cartType) {
        return tcCartRepository.queryCount(custId, cartType);
    }


    @Override
    public List<CartItem> queryItems(CartItemUkBatch ukBatch) {
        List<TcCartDO> list = tcCartRepository.queryBatch(
            ukBatch.getCustId(),
            ukBatch.getCartType(),
            ukBatch.getItemSkuIds()
        );
        return list.stream().map(cart -> cartConverter.toCartItem(cart)).collect(Collectors.toList());
    }

    @Override
    public List<CartItem> queryCartItems(CartItemUkBatch ukBatch) {
        List<TcCartDO> list = new ArrayList<>();
        for (ItemSkuId itemSkuId : ukBatch.getItemSkuIds()) {
            List<TcCartDO> tcCartDOS = tcCartRepository.queryBatch(ukBatch.getCustId(), itemSkuId.getSellerId(), itemSkuId.getPayMode(), ukBatch.getItemSkuIds());
            if (CollUtil.isNotEmpty(tcCartDOS)) {
                list.addAll(tcCartDOS);
            }
        }
        return list.stream()
                .map(cart -> cartConverter.toCartItem(cart))
                .collect(Collectors.toList());
    }


    @Override
    public CartItem queryItem(CartItemUk cartItemUk) {
        TcCartDO cart = tcCartRepository.queryByUk(
            cartItemUk.getCustId(),
            cartItemUk.getCartType(),
            cartItemUk.getItemSkuId()
        );
        return cartConverter.toCartItem(cart);
    }

    /**
     * 分组排序
     * @param cart
     */
    @Override
    public void sortGrouping(Cart cart) {
        List<CartItem> list = cart.getGroups()
            .stream()
            .flatMap(group -> group.getCartItems().stream())
            .collect(Collectors.toList());
        // 按 addCartTime 从晚到早排
        Collections.sort(list, Comparator.comparing(CartItem::getAddCartTime).reversed());
        // 按卖家分组
        LinkedHashMultimap<Long, CartItem> multimap = LinkedHashMultimap.create();
        for (CartItem item : list) {
            multimap.put(item.getSellerId(), item);
        }
        // 转换
        List<CartGroup> groups = new ArrayList<>();
        for (Long sellerId : multimap.keySet()) {
            List<CartItem> items = new ArrayList<>();
            for (CartItem item : multimap.get(sellerId)) {
                items.add(item);
            }
            CartGroup group = new CartGroup();
            group.setGroupType(CartGroupTypeEnum.SELLER_GROUP.getCode());
            group.setSellerId(sellerId);
            group.setCartItems(items);
            groups.add(group);
        }
        cart.setGroups(groups);
    }

    @Override
    public void save(CartItem cartItem) {
        TcCartDO cart = cartConverter.toTcCartDO(cartItem);
        if (cart.getId() == null) {
            tcCartRepository.create(cart);
            cartItem.setId(cart.getId());
        } else {
            cart.setGmtModified(new Date());
            tcCartRepository.updateById(cart);
        }
    }

    @Override
    public void save(CartItem cartItem, Long deleteId) {
        tcCartRepository.deleteById(deleteId);
        tcCartRepository.updateById(cartItem);
    }

    @Override
    public void deleteItems(CartItemUkBatch ukBatch) {
        List<CartItem> list = queryCartItems(ukBatch);
        for (CartItem item : list) {
            tcCartRepository.deleteById(item.getId());
        }
    }

    /**
     * 单用户批量删除
     */
    @Override
    public  void deleteItems(List<Long> deleteCartIds) {
        tcCartRepository.deleteByIds(deleteCartIds);
//        for (Long deleteId : deleteCartIds) {
//            tcCartRepository.deleteById(deleteId);
//        }
    }

    @Override
    public void clearItems(Long custId, Integer cartType) {
        List<TcCartDO> list = tcCartRepository.queryAll(custId, cartType);
        for (TcCartDO tcCartDO : list) {
            tcCartRepository.deleteById(tcCartDO.getId());
        }
    }
}
