package com.aliyun.gts.gmall.manager.front.common.consts;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontMappingResponseCode.*;
import static com.aliyun.gts.gmall.manager.front.common.exception.FrontMappingResponseCode.TRADE_PRICE_RISK_ERROR;

/**
 * 失败code处理
 *
 * @author tiansong
 */
public class FailCodeConst {
    /**
     * 这里配置哪些 errCode 不抛出, 映射为场景化的失败提示
     */
    private static Set<String> blackCodeSet = Sets.newHashSet();
    static {
        /* 交易的公共code */
        blackCodeSet.add("20001001");
        blackCodeSet.add("20001002");
        blackCodeSet.add("20001003");
        blackCodeSet.add("20001004");
    }

    /**
     * 这里配置哪些 errCode 需要mapping转化
     */
    private static Map<String, ResponseCode> mappingCode = Maps.newHashMap();
    static {
        mappingCode.put("20102012", ITEM_TRADE_FAILED);
        mappingCode.put("60030002", ITEM_EVOUCHER_EXPIRE);
        mappingCode.put("20120003", CART_QTY_OUT_LIMIT);
        mappingCode.put("20820006", TRADE_PRICE_RISK_ERROR);
    }

    public static ResponseCode mappingCode(String failCode) {
        return mappingCode.get(failCode);
    }

    public static boolean isBlackCode(String failCode) {
        return blackCodeSet.contains(failCode);
    }
}
