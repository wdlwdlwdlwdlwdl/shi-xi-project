package com.aliyun.gts.gmall.manager.framework.localcache;

import com.alibaba.arms.sdk.v1.async.TraceCallable;
import com.alibaba.arms.sdk.v1.async.TraceExecutors;
import com.aliyun.gts.gmall.manager.front.common.config.PerfOptConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * local cache 基类
 *
 * @author tiansong
 */
@Slf4j
public abstract class BaseLocalCacheManager<TK, TV> {

    @Autowired
    private PerfOptConfig perfOptConfig;

    private ExecutorService executor = TraceExecutors.wrapExecutorService(
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy()), true);

    private LoadingCache<TK, TV> localCache = CacheBuilder.newBuilder()
            .maximumSize(this.getMaxSize())
            .refreshAfterWrite(this.getDurationSecond(), TimeUnit.SECONDS)
            .build(new CacheLoader<TK, TV>() {
                @Override
                public TV load(TK key) {
                    return queryRpc(key);
                }

                @Override
                public ListenableFuture<TV> reload(TK key, TV oldValue) {
                    ListenableFutureTask<TV> task = ListenableFutureTask.create(
                            TraceCallable.asyncEntry(() -> queryRpc(key)));
                    executor.execute(task);
                    return task;
                }
            });
    /**
     * 从RPC获取数据，包括不限于（db、redis等）
     *
     * @param key
     * @return
     */
    abstract protected TV queryRpc(TK key);

    /**
     * 缓存数据量
     *
     * @return
     */
    protected long getMaxSize() {
        return 100L;
    }

    /**
     * 缓存过期时间（秒）
     *
     * @return
     */
    protected long getDurationSecond() {
        return 1L;
    }

    public TV get(TK key) {
        if (perfOptConfig.isOpenLocalCache()) {
            try {
                return localCache.get(key);
            } catch (Exception e) {
                log.error("BaseLocalCacheManager get error:", e);
            }
        }
        return queryRpc(key);
    }
}