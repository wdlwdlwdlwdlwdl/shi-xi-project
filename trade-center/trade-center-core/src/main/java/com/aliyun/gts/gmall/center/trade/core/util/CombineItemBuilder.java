package com.aliyun.gts.gmall.center.trade.core.util;

import com.aliyun.gts.gmall.center.item.api.dto.output.CombineSkuDTO;
import com.aliyun.gts.gmall.center.item.common.consts.ItemCenterFeatureConstant;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ReversalCombItemDTO;
import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalSubOrderInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO 组合商品
 * @date 2021/10/21 19:23
 */
public class CombineItemBuilder {
    /**
     * @param skuLists
     * @return
     */
    public static Map<Long, List<CombineSkuDTO>> parseCombItemFromItemFeature(Collection<ItemSku> skuLists) {
        Map<Long, List<CombineSkuDTO>> maps = new HashMap<>();
        for (ItemSku sku : skuLists) {
            if (!isCombineItem(sku.getItemFeatureMap())) {
                continue;
            }
            List<CombineSkuDTO> skuDTOS = convertCombineSku(sku);
            maps.put(sku.getSkuId(), skuDTOS);
        }
        return maps;
    }

    private static List<CombineSkuDTO> convertCombineSku(ItemSku sku) {
        com.aliyun.gts.gmall.center.item.api.dto.output.CombineItemDTO combineItemDTO = ItemUtils.getExtendObject(sku, ItemExtendConstant.COMBINE_ITEM, com.aliyun.gts.gmall.center.item.api.dto.output.CombineItemDTO.class);
        return combineItemDTO.getSkuList();
    }

    /**
     * 解析组合商品
     * @param sku
     * @return
     */
    public static List<CombineItemDTO> parseCombineItemByStored(ItemSku sku) {
        if (sku.getStoredExt() == null) {
            return null;
        }
        String str = sku.getStoredExt().get(ItemConstants.COMBINE_ITEM);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JsonUtils.toBeans(str, CombineItemDTO.class);
    }

    public static List<ReversalCombItemDTO> parseReversal(ReversalFeatureDO featureDO){
        if (featureDO.getFeature() == null) {
            return null;
        }
        String str = featureDO.getFeature().get(ItemConstants.COMBINE_ITEM);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JsonUtils.toBeans(str, ReversalCombItemDTO.class);
    }

    /**
     *
     * @param historyList
     * @return
     */
    public static Map<Long,Map<ItemSkuId,Integer>> parseHistoryReversalQty(List<MainReversal> historyList){
        Map<Long,Map<ItemSkuId,Integer>> result = new HashMap<>();
        for(MainReversal reversal : historyList){
            for(SubReversal subReversal : reversal.getSubReversals()){
                Map<String, String> feature = subReversal.getReversalFeatures().getFeature();
                if(feature == null){
                    continue;
                }
                String str = feature.get(ItemConstants.COMBINE_ITEM);
                if(StringUtils.isEmpty(str)){
                    continue;
                }
                List<ReversalCombItemDTO> cbItems = JsonUtils.toBeans(str, ReversalCombItemDTO.class);
                if (CollectionUtils.isEmpty(cbItems)) {
                    continue;
                }
                Map<ItemSkuId, Integer> maps = new HashMap<>();
                for (ReversalCombItemDTO cbItem : cbItems) {
                    ItemSkuId id = new ItemSkuId(cbItem.getItemId(), cbItem.getSkuId());
                    Integer value = maps.get(id);
                    value = value == null ? 0 : value;
                    maps.put(id, value + cbItem.getCancelQty());
                }
                result.put(subReversal.getSubOrder().getOrderId(), maps);
            }
        }
        return result;
    }

    public static Map<Long,Map<ItemSkuId,Integer>> parseInputReversalQty(CreateReversalRpcReq req) {
        Map<Long,Map<ItemSkuId,Integer>> result = new HashMap<>();
        if (CollectionUtils.isEmpty(req.getSubOrders())) {
            return result;
        }
        for(ReversalSubOrderInfo sub : req.getSubOrders()){
            if(sub.getExtra() == null){
                continue;
            }
            String str = sub.getExtra().get(ItemConstants.COMBINE_ITEM);
            if(StringUtils.isEmpty(str)){
                continue;
            }
            List<ReversalCombItemDTO> cbItems = JsonUtils.toBeans(str, ReversalCombItemDTO.class);
            if (CollectionUtils.isEmpty(cbItems)) {
                continue;
            }
            Map<ItemSkuId,Integer> maps = new HashMap<>();
            for(ReversalCombItemDTO cbItem : cbItems){
                maps.put(new ItemSkuId(cbItem.getItemId(),cbItem.getSkuId()),cbItem.getCancelQty());
            }
            result.put(sub.getOrderId(),maps);
        }
        return result;
    }
    /**
     * @param itemFeature
     * @return
     */
    private static boolean isCombineItem(Map<String, String> itemFeature) {
        if (itemFeature == null) {
            return false;
        }
        Object str = itemFeature.get(ItemCenterFeatureConstant.COMBINE_ITEM);
        return ItemCenterFeatureConstant.COMBINE_ITEM_TRUE.equals(str);
    }


    public static void main(String[] args){
        ItemSkuId skuId =new ItemSkuId(1L,2L);
        ItemSkuId skuId2 =new ItemSkuId(1L,2L);
        System.out.print(skuId.equals(skuId2));
    }
}
