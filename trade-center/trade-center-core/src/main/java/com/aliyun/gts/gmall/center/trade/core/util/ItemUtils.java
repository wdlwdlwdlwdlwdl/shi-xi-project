package com.aliyun.gts.gmall.center.trade.core.util;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemExtendDTO;
import com.aliyun.gts.gmall.platform.item.api.utils.ItemExtendUtil;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ItemUtils {

    public static <T> T getExtendObject(ItemSku itemSku, String key, Class<T> clazz) {
        Map<String, String> extMap = itemSku.getItemExtendMap();
        if (extMap == null || extMap.isEmpty()) {
            return null;
        }
        Map<String, ItemExtendDTO> rawMap = new HashMap<>();
        for (Entry<String, String> en : extMap.entrySet()) {
            ItemExtendDTO raw = new ItemExtendDTO();
            raw.setAttrKey(en.getKey());
            raw.setAttrValue(en.getValue());
            rawMap.put(en.getKey(), raw);
        }
        return ItemExtendUtil.getExtendObject(key, rawMap, clazz);
    }

    public static String getItemFeature(ItemSku sku, String key) {
        if (sku.getItemFeatureMap() == null) {
            return null;
        }
        return sku.getItemFeatureMap().get(key);
    }
}
