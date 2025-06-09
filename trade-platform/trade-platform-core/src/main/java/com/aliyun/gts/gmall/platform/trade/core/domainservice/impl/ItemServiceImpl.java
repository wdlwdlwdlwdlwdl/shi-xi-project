package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<ItemSkuId, ItemSku> queryItems(List<ItemSkuId> idList) {
        List<ItemSku> itemList = itemRepository.queryItems(idList);
        return fillSeller(itemList);
    }

    /**
     * 批量查询商品及其卖家信息，有商品不存在时全部报错
     * @param itemSkuIds
     * @return  Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:25:39
     */
    @Override
    public Map<ItemSkuId, ItemSku> queryItemsRequired(List<ItemSkuId> itemSkuIds) {
        List<ItemSku> itemList = itemRepository.queryItemsRequired(itemSkuIds);
        return fillSellerToCart(itemList);
    }

    /**
     * 批量查询商品及其卖家信息，有商品不存在时全部报错
     * @param itemSkuIds
     * @return  Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:25:39
     */
    @Override
    public  Map<ItemSkuId, ItemSku> queryItemsRequiredNew(List<ItemSkuId> itemSkuIds) {
        List<ItemSku> itemList = itemRepository.queryItemsRequired(itemSkuIds);
        return fillSellerToCart(itemList);
    }

    /**
     * 查商品缓存的接口
     */
    @Override
    public Map<ItemSkuId, ItemSku> queryItemsToCartFromCache(List<ItemSkuId> itemSkuIds) {
        // 查询商品信息
        List<ItemSku> itemList = itemRepository.queryItemsToCartFromCache(itemSkuIds);
        // 填充卖家信息
        return fillSellerToCart(itemList);
    }

    /**
     * 查商品缓存的接口
     */
    @Override
    public Map<ItemSkuId, ItemSku> queryItemsFromCache(List<ItemSkuId> idList) {
        List<ItemSku> itemList = itemRepository.queryItemsFromCache(idList);
        // 填充卖家信息
        return fillSeller(itemList);
    }

    /**
     * 批量查询卖家信息
     * @param itemList
     * @return
     */
    @Override
    public  Map<ItemSkuId, ItemSku> fillSellerToCart(List<ItemSku> itemList) {
        List<Long> sellerIds = itemList.stream()
            .map(item -> item.getSeller().getSellerId())
            .distinct()
            .collect(Collectors.toList());
        // 查询下卖家
        List<Seller> sellerList = userRepository.getSellerByIds(sellerIds);
        Map<Long, Seller> sellerMap = CommUtils.toMap(sellerList, Seller::getSellerId);
        itemList.stream().forEach(item -> {
            Seller seller = sellerMap.get(item.getSeller().getSellerId());
            if (Objects.nonNull(seller)) {
                item.setSeller(seller);
            }
        });
        // 以 商品+SKU+卖家分组 ！ 一品多商
        return CommUtils.toMap(itemList, item -> new ItemSkuId(
            item.getItemId(),
            item.getSkuId(),
            item.getSeller().getSellerId())
        );
    }

    /**
     * 填充卖家信息
     * @param itemList
     * @return
     */
    @Override
    public Map<ItemSkuId, ItemSku> fillSeller(List<ItemSku> itemList) {
        Set<Long> sellerIds = itemList.stream().map(item -> item.getSeller().getSellerId()).collect(Collectors.toSet());
        // 查询下卖家
        List<Seller> sellerList = userRepository.getSellers(sellerIds);
        Map<Long, Seller> sellerMap = CommUtils.toMap(sellerList, Seller::getSellerId);
        itemList.stream().forEach(item -> {
            Seller s = sellerMap.get(item.getSeller().getSellerId());
            if (s != null) {
                item.setSeller(s);
            }
        });
        return CommUtils.toMap(itemList, ItemSku::getItemSkuId);
    }

}
