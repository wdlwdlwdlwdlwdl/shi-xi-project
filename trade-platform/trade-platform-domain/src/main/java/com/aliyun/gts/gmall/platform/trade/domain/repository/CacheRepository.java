package com.aliyun.gts.gmall.platform.trade.domain.repository;

import java.util.concurrent.TimeUnit;

public interface CacheRepository {

    /**
     * 查询
     */
    <T> T get(String key);

    /**
     * put 长期存活
     */
    void put(String key, Object value);

    /**
     * put 指定存活时长
     */
    void put(String key, Object value, long timeToLive, TimeUnit timeUnit);

    /**
     * 删除, 并返回成功删除的数量
     */
    int delete(String key);

    /**
     * CAS, 更新 atomSetLong 的值, 不能是 put 的值
     */
    boolean compareAndSetLong(String key, long expect, long update, long timeToLive, TimeUnit timeUnit);

    /**
     * 设置Long 值, 可用于 CAS
     */
    void atomSetLong(String key, long value, long timeToLive, TimeUnit timeUnit);
}
