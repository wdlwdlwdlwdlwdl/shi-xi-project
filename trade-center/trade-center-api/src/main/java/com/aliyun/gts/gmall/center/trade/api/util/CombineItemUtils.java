package com.aliyun.gts.gmall.center.trade.api.util;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/25 19:21
 */
public class CombineItemUtils {
    /**
     * 返回组合商品
     * @param storedExt
     * @return
     */
    public static List<CombineItemDTO> parseCombineItem(Map<String, String> storedExt) {
        if(storedExt == null){
            return  null;
        }
        String str = storedExt.get(ItemConstants.COMBINE_ITEM);
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JSONArray.parseArray(str, CombineItemDTO.class);
    }
}
