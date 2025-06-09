package com.aliyun.gts.gmall.platform.trade.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class AbstractBusinessEntity implements Serializable {

    /**
     * 模型扩展字段
     */
    private Map<String, Object> extraMap;

    public void putExtra(String key, Object value) {
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        extraMap.put(key, value);
    }

    public void putAllExtra(Map<String, ?> other) {
        if (other == null) {
            return;
        }
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        extraMap.putAll(other);
    }

    public Object getExtra(String key) {
        return extraMap == null ? null : extraMap.get(key);
    }
}
