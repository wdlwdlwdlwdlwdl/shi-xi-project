package com.aliyun.gts.gmall.platform.trade.common.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DynamicObject implements Serializable {

    @Getter
    @Setter
    private Map<String, Object> map;

    public <T extends DynamicObject> T build(Map<String, Object> map) {
        this.map = map;
        return (T) this;
    }

    public <T> T getObject(String key) {
        return map == null ? null : (T) map.get(key);
    }

    public Integer getInt(String key, Integer defaultValue) {
        Number n = getObject(key);
        if (n != null) {
            return n.intValue();
        }
        return defaultValue;
    }

    public Long getLong(String key, Long defaultValue) {
        Number n = getObject(key);
        if (n != null) {
            return n.longValue();
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Object o = getObject(key);
        if (o instanceof String) {
            return Boolean.parseBoolean((String) o);
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        return defaultValue;
    }

    public <T extends DynamicObject> T getDynamicObject(String key, Supplier<T> dy) {
        Map<String, Object> data = getObject(key);
        return data == null ? null : dy.get().build(data);
    }

    public <T extends DynamicObject> List<T> getDynamicObjects(String key, Supplier<T> dy) {
        List<Map<String, Object>> data = getObject(key);
        return data == null ? null : data.stream()
                .map(m -> (T) dy.get().build(m))
                .collect(Collectors.toList());
    }
}
