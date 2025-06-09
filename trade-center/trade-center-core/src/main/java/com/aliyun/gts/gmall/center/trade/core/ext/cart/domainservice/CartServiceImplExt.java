package com.aliyun.gts.gmall.center.trade.core.ext.cart.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.ext.cartAdd.TcCartRepositoryExt;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.common.constants.CartGroupTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CartConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.CartServiceImpl;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcCartRepository;
import com.google.common.collect.LinkedHashMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
public class CartServiceImplExt extends CartServiceImpl {

    @Autowired
    private TcCartRepository tcCartRepository;

    @Autowired
    private CartConverter cartConverter;

    @Autowired
    private TcCartRepositoryExt tcCartRepositoryExt;


    public CartItem queryItem(CartItemUk cartItemUk) {
        TcCartDO cart = tcCartRepositoryExt.queryByUk(cartItemUk);
        return cartConverter.toCartItem(cart);
    }

    @Override
    public void sortGrouping(Cart cart) {
        List<CartItem> list = cart.getGroups()
            .stream()
            .flatMap(group -> group.getCartItems().stream())
            .collect(Collectors.toList());
        // 按 addCartTime 从晚到早排
        Collections.sort(list, Comparator.comparing(CartItem::getAddCartTime).reversed());
        List<CartItem> mergeAllItems = new ArrayList<>();
        // 按支付方式分组
        LinkedHashMultimap<String, CartItem> multimap = LinkedHashMultimap.create();
        Set<CartItem> invalidList= new HashSet<>();
        for (CartItem item : list) {
            // 判断是否失效
            if (Boolean.TRUE.equals(item.getSkuChangeStatus()) ||
                Boolean.TRUE.equals(item.getSellerChangeStatus()) ||
                Boolean.TRUE.equals(item.getItemChangeStatus())) {
                item.setOriginalPayMode(item.getPayMode());
                item.setPayMode(PayMode.INVALID);
                invalidList.add(item);
            } else {
                multimap.put(item.getPayMode(), item);
            }
        }
        // 失效放最后
        if(CollectionUtils.isNotEmpty(invalidList)) {
           multimap.putAll(PayMode.INVALID, invalidList);
        }
        // 转换
        List<CartGroup> groups = new ArrayList<>();
        for (String payMode : multimap.keySet()) {
            List<CartItem> cartItemList = new ArrayList<>();
            Long cartId = null;
            List<CartItem> mergeItems;
            if (PayMode.INVALID.equals(payMode)) {
                mergeItems = new ArrayList<>(multimap.get(payMode));
            } else {
                Set<CartItem> items = multimap.get(payMode);
                mergeItems = mergeItems(items);
                mergeAllItems.addAll(mergeItems);
            }
            for (CartItem item : mergeItems) {
                cartId = item.getCartId();
                cartItemList.add(item);
            }
            CartGroup group = new CartGroup();
            group.setGroupType(CartGroupTypeEnum.PAY_MODE_GROUP.getCode());
            group.setPayMode(payMode);
            group.setCartItems(cartItemList);
            group.setCartId(cartId);
            groups.add(group);
        }
        cart.setGroups(groups);
        sortGroupingBySellId(cart, mergeAllItems);
    }


    public String getKey(CartItem cartItem) {
        return cartItem.getItemId() + "_" + cartItem.getSellerId() + "_" + cartItem.getSkuId() + "_" + cartItem.getPayMode()+"_"+cartItem.getSkuQuoteId();
    }
    public  List<CartItem> mergeItems(Set<CartItem> items) {
        //合并相同商品
        Map<String, CartItem> mergedMap = new HashMap<>();
        //需要删除的购物车
        List<Long> cartIdList = new ArrayList<>();
        for (CartItem item : items) {
            //获取购物车唯一的key
            String key = getKey(item);
            //存在相同的key合并
            if (mergedMap.containsKey(key)) {
                CartItem mergedItem = mergedMap.get(key);
                mergedItem.setQuantity(mergedItem.getQuantity()+item.getQuantity());
                // 商品单价*商品数量 原价价格
                mergedItem.setCartTotalPrice(mergedItem.getCartTotalPrice() +item.getCartTotalPrice());
                // 营销家 先设置原价 后面营销计算替换替换
                mergedItem.setCartPromotionPrice(mergedItem.getCartPromotionPrice() + item.getCartPromotionPrice());
                //合并后的购物车删除
                cartIdList.add(item.getCartId());
            } else {
                //唯一的直接保存。
                CartItem itemMerge = new CartItem();
                try {
                    PropertyUtils.copyProperties(itemMerge, item);
                } catch (Exception e) {
                    log.error("mergeItems copyProperties error {}", e.getMessage());
                    continue;
                }
                mergedMap.put(key, itemMerge);
            }
        }
        //删除合并后的购物车
        if(CollectionUtils.isNotEmpty(cartIdList)) {
            tcCartRepositoryExt.deleteByIds(cartIdList);
        }
        Collection<CartItem> mergeCartItems = mergedMap.values();
        //合并后的如果分期变化，更新数据库
        if(CollectionUtils.isNotEmpty(mergeCartItems))
        {
            for(CartItem cartItem: mergeCartItems) {
                if(Objects.nonNull(cartItem.getCurrentInstallment())&&!cartItem.getCurrentInstallment().equals(cartItem.getOriginInstallment())) {
                    this.save(cartItem);
                }
            }
        }
        return new ArrayList<>(mergeCartItems);
    }

    /**
     * 分组排序
     * @param cart
     */
    public void sortGroupingBySellId(Cart cart, List<CartItem> list) {
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
        cart.setGroupsBySeller(groups);
    }

    @Override
    public List<CartItem> queryItems(CartItemUkBatch ukBatch) {
        List<TcCartDO> list = tcCartRepositoryExt.queryBatch(ukBatch);
        return list.stream().map(cart -> cartConverter.toCartItem(cart)).collect(Collectors.toList());
    }

    /**
     * 在购物车保存的时候把
     *  ddPrice 添加购物车时的价格
     *  addInstallment
     *  addLoan 等保存至 购物车 features 字段
     * @param cartItem
     */
    @Override
    public void save(CartItem cartItem) {
        TcCartDO cart = cartConverter.toTcCartDO(cartItem);
        if (null != cartItem.getItemSku() && null != cartItem.getItemSku().getItemPrice()) {
            cart.getFeatures().setAddOriginPrice(cartItem.getItemSku().getItemPrice().getOriginPrice());
        }
        if (cart.getId() == null) {
            tcCartRepository.create(cart);
            cartItem.setId(cart.getId());
        } else {
            tcCartRepository.updateById(cart);
        }
    }
}
