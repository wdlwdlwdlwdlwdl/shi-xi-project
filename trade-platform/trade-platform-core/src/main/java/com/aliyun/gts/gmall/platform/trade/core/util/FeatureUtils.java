package com.aliyun.gts.gmall.platform.trade.core.util;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureUtils {
    private static final char SP1 = ';';
    private static final char SP2 = ':';

    public static Map<String, String> parse(String features) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(features)) {
            return map;
        }
        for (String f : StringUtils.splitPreserveAllTokens(features, SP1)) {
            String[] kv = StringUtils.splitPreserveAllTokens(f, SP2);
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    public static String format(Map<String, String> map) {
        StringBuilder s = new StringBuilder();
        for (Entry<String, String> en : map.entrySet()) {
            s.append(filterSpecialChar(en.getKey()))
                    .append(SP2)
                    .append(filterSpecialChar(en.getValue()))
                    .append(SP1);
        }
        // 去掉最后的分隔符
        if (s.length() > 0) {
            s.setLength(s.length() - 1);
        }
        return s.toString();
    }

    public static String format(String... kvs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            if (i + 1 < kvs.length) {
                map.put(kvs[i], kvs[i + 1]);
            }
        }
        return format(map);
    }

    private static String filterSpecialChar(String s) {
        if (s.indexOf(SP1) < 0 && s.indexOf(SP2) < 0) {
            return s;
        }
        // 不转义, 直接禁止出现分隔符
        throw new GmallException(CommonErrorCode.SERVER_ERROR);
    }
}
