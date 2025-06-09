package com.aliyun.gts.gmall.manager.front.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 性能优化相关的开关
 * 包括缓存、降级、容灾等配置
 *
 * @author tiansong
 */
@Data
@Component
@RefreshScope
public class PerfOptConfig {
    /**
     * for cache
     */
    @Value("${perf.local.openCache:true}")
    boolean openLocalCache;


    /**
     * for degradation
     */

}
