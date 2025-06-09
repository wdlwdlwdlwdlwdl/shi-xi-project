package com.aliyun.gts.gmall.platform.trade.domain.util;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class CommUtils {

    public static <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyExtractor) {
        if (list == null) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(
                v -> keyExtractor.apply(v), v -> v));
    }

    public static <V> V firstNotNull(V... v) {
        if (v == null || v.length == 0) {
            return null;
        }
        for (int i = 0; i < v.length; i++) {
            if (v[i] != null) {
                return v[i];
            }
        }
        return null;
    }

    public static void assertNotNull(Object o, ResponseCode code, Object... args) {
        if (o == null) {
            throw new GmallException(code, args);
        }
    }

    public static void assertEqual(Object o1, Object o2, ResponseCode code, Object... args) {
        if (ObjectUtils.notEqual(o1, o2)) {
            throw new GmallException(code, args);
        }
    }

    public static <T> T logTime(Supplier<T> s, String name) {
        long begin = System.nanoTime();
        try {
            return s.get();
        } finally {
            long end = System.nanoTime();
            log.info("{}-time={}ms", name, (end - begin) / 1000000d);
        }
    }

    public static void logTime(Runnable s, String name) {
        long begin = System.nanoTime();
        try {
            s.run();
        } finally {
            long end = System.nanoTime();
            log.info("{}-time={}ms", name, (end - begin) / 1000000d);
        }
    }

    public static <K, V> V getValue(Map<K, V> map, K key, Supplier<V> init) {
        V v = map.get(key);
        if (v == null) {
            v = init.get();
            map.put(key, v);
        }
        return v;
    }

    /**
     * m1 + m2
     */
    public static <K> Map<K, Long> addMergeLong(Map<K, Long> m1, Map<K, Long> m2) {
        return merge(m1, m2, (v1, v2) -> NumUtils.getNullZero(v1) + NumUtils.getNullZero(v2));
    }

    /**
     * m1 - m2
     */
    public static <K> Map<K, Long> subMergeLong(Map<K, Long> m1, Map<K, Long> m2) {
        return merge(m1, m2, (v1, v2) -> NumUtils.getNullZero(v1) - NumUtils.getNullZero(v2));
    }

    public static <K, V> Map<K, V> merge(Map<K, V> m1, Map<K, V> m2, BiFunction<V, V, V> mergeValue) {
        if (m1 == null) {
            m1 = Collections.EMPTY_MAP;
        }
        if (m2 == null) {
            m2 = Collections.EMPTY_MAP;
        }

        Set<K> all = new HashSet<>();
        all.addAll(m1.keySet());
        all.addAll(m2.keySet());

        Map<K, V> result = new HashMap<>();
        for (K key : all) {
            V value = mergeValue.apply(m1.get(key), m2.get(key));
            result.put(key, value);
        }
        return result;
    }

    public static void retryOnException(Runnable run, int times, long sleep) {
        Throwable last = null;
        for (int i=0; i<times; i++) {
            try {
                run.run();
                return;
            } catch (Throwable t) {
                last = t;
                log.warn("attempts fail", t);
                if (sleep > 0) {
                    try { Thread.sleep(sleep); }
                    catch (Exception e) {}
                }
            }
        }
        ErrorUtils.throwUndeclared(last);
    }
}
