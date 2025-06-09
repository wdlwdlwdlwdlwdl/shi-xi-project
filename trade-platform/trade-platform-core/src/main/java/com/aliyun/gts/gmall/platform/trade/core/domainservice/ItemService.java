package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;

import java.util.List;
import java.util.Map;

public interface ItemService {

    /**
     * 批量查询商品及其卖家信息，返回存在的商品
     */
    Map<ItemSkuId, ItemSku> queryItems(List<ItemSkuId> idList);

    /**
     * 批量查询商品及其卖家信息，有商品不存在时全部报错
     * @param itemSkuIds
     * @return  Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:25:39
     */
    Map<ItemSkuId, ItemSku> queryItemsRequired(List<ItemSkuId> itemSkuIds);

    /**
     * 批量查询商品及其卖家信息，有商品不存在时全部报错
     * @param itemSkuIds
     * @return  Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:25:39
     */
    Map<ItemSkuId, ItemSku> queryItemsRequiredNew(List<ItemSkuId> itemSkuIds);

    /**
     * 查商品缓存的接口
     */
    Map<ItemSkuId, ItemSku> queryItemsToCartFromCache(List<ItemSkuId> itemSkuIds);

    /**
     * 查商品缓存的接口
     */
    Map<ItemSkuId, ItemSku> queryItemsFromCache(List<ItemSkuId> idList);

    /**
     * 批量查询卖家信息
     * @param itemList
     * @return
     */
    Map<ItemSkuId, ItemSku> fillSellerToCart(List<ItemSku> itemList);

    /**
     * 查询卖家信息
     * @param itemList
     * @return
     */
    Map<ItemSkuId, ItemSku> fillSeller(List<ItemSku> itemList);
}
