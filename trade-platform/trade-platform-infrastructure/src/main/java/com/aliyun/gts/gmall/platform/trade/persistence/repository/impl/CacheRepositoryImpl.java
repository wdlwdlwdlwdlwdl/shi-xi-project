package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CacheRepositoryImpl implements CacheRepository {

    @Autowired
    private CacheManager tradeCacheManager;

    @Override
    public <T> T get(String key) {
        return tradeCacheManager.get(key);
    }

    @Override
    public void put(String key, Object value) {
        tradeCacheManager.set(key, value);
    }

    @Override
    public void put(String key, Object value, long timeToLive, TimeUnit timeUnit) {
        tradeCacheManager.set(key, value, timeToLive, timeUnit);
    }

    @Override
    public int delete(String key) {
        return (int) tradeCacheManager.delete(key);
    }

    @Override
    public boolean compareAndSetLong(String key, long expect, long update, long timeToLive, TimeUnit timeUnit) {
        if (tradeCacheManager.compareAndSet(key, expect, update)) {
            tradeCacheManager.expire(key, timeToLive, timeUnit);
            return true;
        }
        return false;
    }

    @Override
    public void atomSetLong(String key, long value, long timeToLive, TimeUnit timeUnit) {
        tradeCacheManager.atomicSet(key, value);
        tradeCacheManager.expire(key, timeToLive, timeUnit);
    }
}
