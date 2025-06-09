package com.aliyun.gts.gmall.platform.trade.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;

public class ParamUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();  // 线程安全

    public static Params parse(String str) {
        Params p = new Params();
        try {
            p.map = MAPPER.readTree(str);
        } catch (IOException e) {
            // ignore
        }
        if (p.map == null) {
            p.map = MissingNode.getInstance();
        }
        return p;
    }


    public static class Params {
        private JsonNode map;

        public String getString(String key, String defaultValue) {
            return map.path(key).asText(defaultValue);
        }

        public int getInt(String key, int defaultValue) {
            return map.path(key).asInt(defaultValue);
        }

        public long getLong(String key, long defaultValue) {
            return map.path(key).asLong(defaultValue);
        }
    }
}
