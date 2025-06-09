package com.aliyun.gts.gmall.manager.front.item.localcache;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemGroupFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RelationPageLocalCacheManager {

    @NacosValue(value = "${relationPage.localcache.enable:true}", autoRefreshed = true)
    private boolean isOpenRelationPageCache;

    @Autowired
    private ItemGroupFacade itemGroupFacade;

    private static final ExecutorService executor = new ThreadPoolExecutor(2, 2, 500, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100),
            new DefaultThreadFactory("relation-page-local-cache-thread"), new ThreadPoolExecutor.AbortPolicy());

    private LoadingCache<Long, List<ItemDetailVO>> relationPageLocalCache = CacheBuilder.newBuilder()
            .maximumSize(this.getMaxSize())
            .expireAfterWrite(this.getExpireDuration(),TimeUnit.SECONDS)
            .refreshAfterWrite(this.getRefreshDuration(), TimeUnit.SECONDS)
            .build(new CacheLoader<Long, List<ItemDetailVO>>() {
                @Override
                public List<ItemDetailVO> load(Long key) {
                    return queryRpc(key);
                }

                @Override
                public ListenableFuture<List<ItemDetailVO>> reload(Long key, List<ItemDetailVO> oldValue) {
                    ListenableFutureTask<List<ItemDetailVO>> task = ListenableFutureTask.create(() -> {
                        return queryRpc(key);
                    });
                    executor.execute(task);
                    return task;
                }
            });

    public List<ItemDetailVO> queryRpc(Long key) {
        GrGroupQuery grGroupQuery = new GrGroupQuery();
        grGroupQuery.setGroupId(key);
        grGroupQuery.setPage(new PageParam());
        return itemGroupFacade.queryRelation(grGroupQuery);
    }

    /**
     * 缓存数据量
     *
     * @return
     */
    public long getMaxSize() {
        return 1000L;
    }

    /**
     * 缓存过期时间（秒）
     *
     * @return
     */
    protected long getRefreshDuration() {
        return 10L;
    }

    protected long getExpireDuration() {
        return 30L;
    }

    public List<ItemDetailVO> relationPage(GrGroupQuery grGroupQuery) {
        if (isOpenRelationPageCache) {
            try {
                return relationPageLocalCache.get(grGroupQuery.getGroupId());
            } catch (Exception e) {
                log.error("RelationPageLocalCacheManager get relationPage:", e);
            }
        }
        return itemGroupFacade.queryRelation(grGroupQuery);
    }

}
