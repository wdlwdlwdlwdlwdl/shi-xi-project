package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemCategory;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface ItemRepository {

    /**
     * 单个查询，不存在时返回null (不含类目信息)
     */
    ItemSku queryItem(ItemSkuId id);

    /**
     * 单个查询，不存在时抛出异常 (不含类目信息)
     */
    ItemSku queryItemRequired(ItemSkuId id);

    /**
     * 批量查询，返回存在的商品 (不含类目信息)
     */
    List<ItemSku> queryItems(List<ItemSkuId> idList);

    /**
     * 根据商品ID查所有商品SKU
     */
    List<ItemSku> queryItemsByItemId(Long itemId);

    /**
     * 批量查询商品，有商品不存在时全部报错 (不含类目信息)
     * @param itemSkuIds
     */
    List<ItemSku> queryItemsRequired(List<ItemSkuId> itemSkuIds);

    /**
     * 批量查询，返回存在的商品（走IC缓存的接口） (不含类目信息)
     */
    List<ItemSku> queryItemsToCartFromCache(List<ItemSkuId> itemSkuIds);

    /**
     * 批量查询，返回存在的商品（走IC缓存的接口） (不含类目信息)
     */
    List<ItemSku> queryItemsFromCache(List<ItemSkuId> itemSkuIds);

//    /**
//     * 计算运费, 不支持的配送地区返回 null
//     */
//    Long calcFreightFee(List<SubOrder> orders, ReceiveAddr receiver);

    /**
     * 查类目信息
     */
    ItemCategory queryItemCategory(Long categoryId);

    /**
     * 查类目信息
     */
    Map<Long, ItemCategory> batchQueryItemCategory(Collection<Long> categoryIds);

    /**
     * 填类目信息
     */
    default void fillCategoryInfo(Collection<ItemSku> itemSkus) {
        if (CollectionUtils.isEmpty(itemSkus)) {
            return;
        }
        Set<Long> cateIds = itemSkus.stream()
                .map(ItemSku::getCategoryId)
                .collect(Collectors.toSet());
        Map<Long, ItemCategory> cateMap = batchQueryItemCategory(cateIds);
        for (ItemSku item : itemSkus) {
            ItemCategory cate = cateMap.get(item.getCategoryId());
            item.setItemCategory(cate);
        }
    }
}
