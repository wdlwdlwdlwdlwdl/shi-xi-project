package com.aliyun.gts.gmall.manager.front.item.dto.utils;

import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.MinPromCampDTO;

import java.util.*;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/7 13:47
 */
public class PromotionFillUtils {
    /**
     * 解析所有的优惠券
     * @param maps
     * @param <T>
     * @return
     */
    public static <T extends MinPromCampDTO> List<T> parseAllCampaign(Map<Long, List<T>> maps){
        if(maps == null){
            return null;
        }
        return parseAllCampaign(maps.values());
    }
    /**
     * 解析所有的优惠券
     * @param values
     * @param <T>
     * @return
     */
    private static <T extends MinPromCampDTO> List<T> parseAllCampaign(Collection<List<T>> values) {
        if (values == null) {
            return null;
        }
        Map<Long, T> map = new LinkedHashMap<>();   // 按活动ID去重
        List<T> result = new ArrayList<T>();
        for (List<T> list : values) {
            if (list == null) {
                continue;
            }
            for (T t : list) {
                if (t.getCampaignId() == null) {
                    result.add(t);
                } else {
                    map.put(t.getCampaignId(), t);
                }
            }
        }
        result.addAll(map.values());
        return result;
    }
}
