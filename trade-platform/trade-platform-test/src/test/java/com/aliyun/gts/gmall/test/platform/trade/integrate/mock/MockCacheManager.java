package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedCountDownLatch;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedReadWriteLock;
import com.aliyun.gts.gmall.middleware.api.cache.model.DistributedScoredEntry;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class MockCacheManager implements CacheManager {
    private Map<String, Object> memCache = new ConcurrentHashMap<>();

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public Iterable<String> getKeys(String prefix, int count) {
        return null;
    }

    @Override
    public boolean expire(String key, long timeToLive) {
        return false;
    }

    @Override
    public boolean expire(String key, long timeToLive, TimeUnit timeUnit) {
        return true;
    }

    @Override
    public boolean expireAt(String key, long timestamp) {
        return false;
    }

    @Override
    public boolean clearExpire(String key) {
        return false;
    }

    @Override
    public long ttl(String key) {
        return 0;
    }

    @Override
    public boolean hasKey(String key) {
        return false;
    }

    @Override
    public <T> T get(String key) {
        return (T) memCache.get(key);
    }

    @Override
    public List<?> batchGetAsList(List<String> keys) {
        return null;
    }

    @Override
    public Map<String, ?> batchGetAsMap(List<String> keys) {
        return null;
    }

    @Override
    public boolean existsNull(String key) {
        return false;
    }

    @Override
    public <V> void set(String key, V value) {
        memCache.put(key, value);
    }

    @Override
    public <V> void set(Map<String, V> values) {

    }

    @Override
    public <V> void set(String key, V value, long timeToLive) {

    }

    @Override
    public <V> void set(String key, V value, long timeToLive, TimeUnit timeUnit) {
        set(key, value);
    }

    @Override
    public <V> void set(Map<String, V> values, long timeToLive) {

    }

    @Override
    public <V> void set(Map<String, V> values, long timeToLive, TimeUnit timeUnit) {

    }

    @Override
    public void setNull(String key, long timeToLive) {

    }

    @Override
    public void setNull(String key, long timeToLive, TimeUnit timeUnit) {

    }

    @Override
    public <V> boolean trySet(String key, V value) {
        return false;
    }

    @Override
    public <V> Map<String, Boolean> trySet(Map<String, V> values) {
        return null;
    }

    @Override
    public <V> boolean trySet(String key, V value, long timeToLive) {
        return false;
    }

    @Override
    public <V> boolean trySet(String key, V value, long timeToLive, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public <V> Map<String, Boolean> trySet(Map<String, V> values, long timeToLive) {
        return null;
    }

    @Override
    public <V> Map<String, Boolean> trySet(Map<String, V> values, long timeToLive, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public long delete(String... keys) {
        for (String key : keys) {
            memCache.remove(key);
        }
        return 0;
    }

    @Override
    public long delete(Collection<String> keys) {
        return 0;
    }

    @Override
    public long deleteByPrefix(String prefix) {
        return 0;
    }

    @Override
    public <V> Map<String, V> getMap(String key) {
        return null;
    }

    @Override
    public <V> V getMapValue(String key, String elementKey) {
        return null;
    }

    @Override
    public <V> Map<String, V> getAllMapValue(String key, Set<String> elementKeys) {
        return null;
    }

    @Override
    public <V> V putMapValue(String key, String elementKey, V value) {
        return null;
    }

    @Override
    public <V> V putMapValueIfAbsent(String key, String elementKey, V value) {
        return null;
    }

    @Override
    public <V> void putAllMapValue(String key, Map<String, ? extends V> map) {

    }

    @Override
    public boolean copyOfMap(String fromKey, String toKey, boolean deleteFromKey) {
        return false;
    }

    @Override
    public <V> V removeMapValue(String key, String elementKey) {
        return null;
    }

    @Override
    public boolean removeAllMapValue(String key) {
        return false;
    }

    @Override
    public long removeAllMapValue(String key, Collection<String> elementKeys) {
        return 0;
    }

    @Override
    public <V> List<V> getList(String key) {
        return null;
    }

    @Override
    public <V> V getListValue(String key, int index) {
        return null;
    }

    @Override
    public <V> List<V> getListValueByRange(String key, int toIndex) {
        return null;
    }

    @Override
    public <V> List<V> getListValueByRange(String key, int fromIndex, int toIndex) {
        return null;
    }

    @Override
    public <V> void addListValue(String key, V value) {

    }

    @Override
    public <V> void addAllListValue(String key, Collection<? extends V> values) {

    }

    @Override
    public <V> void addAllListValue(String key, int index, Collection<? extends V> values) {

    }

    @Override
    public <V> void trimList(String key, int fromIndex, int toIndex) {

    }

    @Override
    public <V> boolean removeListValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean removeAllListValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> Set<V> getSet(String key) {
        return null;
    }

    @Override
    public <V> boolean containsSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean containsAllSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> Iterator<? extends V> iteratorSet(String key) {
        return null;
    }

    @Override
    public <V> boolean addSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean addAllSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> V removeSetRandom(String key) {
        return null;
    }

    @Override
    public <V> Set<V> removeSetRandom(String key, int count) {
        return null;
    }

    @Override
    public <V> boolean removeSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean removeAllSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> Set<V> getOrderSet(String key) {
        return null;
    }

    @Override
    public <V> boolean containsOrderSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean containsAllOrderSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> Iterator<? extends V> iteratorOrderSet(String key) {
        return null;
    }

    @Override
    public <V> boolean addOrderSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean addAllOrderSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public <V> boolean removeOrderSetValue(String key, V value) {
        return false;
    }

    @Override
    public <V> boolean removeAllOrderSetValue(String key, Collection<? extends V> values) {
        return false;
    }

    @Override
    public int countOfZSet(String key) {
        return 0;
    }

    @Override
    public int countOfZSet(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return 0;
    }

    @Override
    public <V> Double getZSetScore(String key, V element) {
        return null;
    }

    @Override
    public <V> Iterator<V> iteratorZSet(String key) {
        return null;
    }

    @Override
    public <V> Iterator<V> iteratorZSet(String key, String pattern) {
        return null;
    }

    @Override
    public <V> Collection<V> getZSetValueRange(String key, int fromIndex, int toIndex, boolean reverse) {
        return null;
    }

    @Override
    public <V> Collection<V> getZSetValueRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, boolean reverse) {
        return null;
    }

    @Override
    public <V> Collection<V> getZSetValueRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int limit, boolean reverse) {
        return null;
    }

    @Override
    public <V> Collection<DistributedScoredEntry<V>> getZSetEntryRange(String key, int fromIndex, int toIndex, boolean reverse) {
        return null;
    }

    @Override
    public <V> Collection<DistributedScoredEntry<V>> getZSetEntryRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, boolean reverse) {
        return null;
    }

    @Override
    public <V> Collection<DistributedScoredEntry<V>> getZSetEntryRange(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive, int offset, int limit, boolean reverse) {
        return null;
    }

    @Override
    public <V> boolean addZSetValue(String key, V element, double score) {
        return false;
    }

    @Override
    public <V> int addAllZSetValue(String key, Map<V, Double> elements) {
        return 0;
    }

    @Override
    public <V> Double addZSetScore(String key, V element, double delta) {
        return null;
    }

    @Override
    public <V> boolean removeZSetValue(String key, V element) {
        return false;
    }

    @Override
    public <V> boolean removeAllZSetValue(String key, Collection<V> values) {
        return false;
    }

    @Override
    public int removeZSetValueByRank(String key, int fromIndex, int toIndex) {
        return 0;
    }

    @Override
    public int removeZSetValueByScore(String key, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return 0;
    }

    @Override
    public long atomicGet(String key) {
        return 0;
    }

    @Override
    public void atomicSet(String key, long value) {
        set(key, value);
    }

    @Override
    public long atomicGetAndAdd(String key, long delta) {
        return 0;
    }

    @Override
    public long atomicAddAndGet(String key, long delta) {
        return 0;
    }

    @Override
    public long atomicGetAndSet(String key, long value) {
        return 0;
    }

    @Override
    public long atomicGetAndDelete(String key) {
        return 0;
    }

    @Override
    public long getAndIncrement(String key) {
        return 0;
    }

    @Override
    public long incrementAndGet(String key) {
        return 0;
    }

    @Override
    public long getAndDecrement(String key) {
        return 0;
    }

    @Override
    public long decrementAndGet(String key) {
        return 0;
    }

    @Override
    public boolean compareAndSet(String key, long expect, long update) {
        Long v = (Long) memCache.get(key);
        if (v != null && v == expect) {
            memCache.put(key, update);
            return true;
        }
        return false;
    }

    @Override
    public DistributedLock getLock(String key) {
        return new MockDistributedLock();
    }

    @Override
    public DistributedReadWriteLock getReadWriteLock(String key) {
        return null;
    }

    @Override
    public DistributedCountDownLatch getCountDownLatch(String key) {
        return null;
    }

    private static class MockDistributedLock implements DistributedLock {

        @Override
        public boolean tryLock(long waitTime, long leaseTime) throws InterruptedException {
            return true;
        }

        @Override
        public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
            return true;
        }

        @Override
        public void unLock() {

        }

        @Override
        public boolean isLocked() {
            return false;
        }
    }
}
