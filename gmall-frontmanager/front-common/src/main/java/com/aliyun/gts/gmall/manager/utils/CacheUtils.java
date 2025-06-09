package com.aliyun.gts.gmall.manager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheUtils {
    public static <K, V> Cache<K, V> defaultLocalCache() {
        return defaultLocalCache(15);
    }

    public static <K, V> Cache<K, V> defaultLocalCache(long seconds) {
        return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).maximumSize(10000).build();
    }

    public static <K, V> V getNullableQuietly(Cache<K, ?> cache, K key, Callable<V> loader) {
        Callable<Optional<V>> nullable = () -> Optional.ofNullable(loader.call());
        return getQuietly(cache, key, nullable).orElse(null);
    }

    public static <K, V> V getQuietly(Cache<K, ?> cache, K key, Callable<V> loader) {
        try {
            return (V) cache.get(key, (Callable) loader);
        } catch (ExecutionException e) {
            Throwable t = e;
            if (e.getCause() != null) {
                t = e.getCause();
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            throw new RuntimeException(t);
        }
    }

    // ------------------------------------------------这个用来处理返回返回对象是list,并自动加载未查询到的对象------------------------------------------------
    /**
     * 
     * @param <K> 缓存主键类型
     * @param <V> 缓存主键
     * @param <R> RPC入参类型
     * @param <T> 返回对象的类型
     * @param cache 缓存
     * @param keyPrefix 缓存前缀
     * @param keys 查询的主键
     * @param rpcReqCreator rpc请求入参的构造器
     * @param keyProvider 主键的键值
     * @param dubboDataSource
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <S, K, R extends AbstractRequest, T> List<T> getNullableQuietly(Cache<S, ?> cache, String keyPrefix, List<K> keys,
            Function<List<K>, R> rpcReqCreator, Function<T, K> keyProvider, DubboDataSource dubboDataSource) {
        // 尝试从缓存中获取值
        Pair<List<K>, List<T>> pair = getNullableList(cache, keyPrefix, keys);
        List<K> missingKeys = pair.getKey();
        List<T> resultList = pair.getValue();
        log.info("[CacheUtils]批量查询->{}, 命中缓存={}, 未命中的缓存keys={}", keyPrefix, JSON.toJSONString(resultList), JSON.toJSONString(missingKeys));

        // 处理未命中的键,如果未命中缓存,直接返回已有结果
        if (missingKeys.isEmpty()) {
            return resultList;
        }

        try {
            // 从数据库加载缺失的数据
            List<T> loadedList = loadMissingData(missingKeys, rpcReqCreator, dubboDataSource);
            log.info("[CacheUtils]load查询数据->{}, 结果={}", keyPrefix, JSON.toJSONString(loadedList));

            // 检查返回的 loadedList 是否为 null 或空
            if (CollectionUtils.isEmpty(loadedList)) {
                log.warn("[CacheUtils]加载的数据为空, 未命中keys: {}", JSON.toJSONString(missingKeys));
                return resultList; // 返回已有结果
            }

            // 将加载的数据存入缓存
            Map<S, T> loadedMap = loadedList.stream().collect(Collectors.toMap(it -> {
                K id = keyProvider.apply(it); // 动态获取主键
                return (S) (keyPrefix + id); // 构建缓存键
            }, Function.identity())); // 使用 Function.identity() 作为映射值

            // 将数据存入缓存
            ((Cache<S, T>) cache).putAll(loadedMap);

            // 返回结果
            resultList.addAll(loadedList);
        } catch (Exception e) {
            handleException(e); // 使用捕获的异常处理函数
        }
        return resultList;
    }

    /**
     * 
     * @param <S> 缓存key的类型
     * @param <K>
     * @param <T> 返回对象类型
     * @param cache 缓存
     * @param keyPrefix 前缀
     * @param keys 需要查询的keys
     * @return <未命中的key, 命中的结果>
     */
    @SuppressWarnings("unchecked")
    private static <S, K, T> Pair<List<K>, List<T>> getNullableList(Cache<S, ?> cache, String keyPrefix, List<K> keys) {
        List<T> resultList = new ArrayList<>();
        List<K> missingKeys = new ArrayList<>();

        // 尝试从缓存中获取每个键的值
        for (K key : keys) {
            // 使用前缀生成 cacheKeys
            K cacheKey = (K) (keyPrefix + key);
            // 使用 Optional 处理可能为 null 的缓存值
            Optional<Object> cachedValueOpt = Optional.ofNullable(cache.getIfPresent(cacheKey));
            // 处理 cachedValue
            cachedValueOpt.ifPresentOrElse(cachedValue -> {
                resultList.add((T) cachedValue);
            }, () -> {
                // 如果 cachedValue 为 null，记录缺失的键
                missingKeys.add(key);
            });
        }
        return Pair.of(missingKeys, resultList);
    }

    /**
     * 加载未命中的数据
     * 
     * @param <K>
     * @param <R> RPC入参类型
     * @param <T> 返回对象类型
     * @param missingKeys 没有命中的keys
     * @param rpcReqCreator rpc入参的构建器
     * @param dubboDataSource
     * @return
     * @throws Exception
     */
    private static <K, R extends AbstractRequest, T> List<T> loadMissingData(List<K> missingKeys, Function<List<K>, R> rpcReqCreator,
            DubboDataSource dubboDataSource) throws Exception {
        R rpcReq = rpcReqCreator.apply(missingKeys);
        return dubboDataSource.queryList(rpcReq, new ArrayList<>());
    }

    /**
     * 异常处理
     * 
     * @param e
     */
    private static void handleException(Exception e) {
        Throwable t = e.getCause() != null ? e.getCause() : e;
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if (t instanceof Error) {
            throw (Error) t;
        }
        throw new RuntimeException(t);
    }
}
