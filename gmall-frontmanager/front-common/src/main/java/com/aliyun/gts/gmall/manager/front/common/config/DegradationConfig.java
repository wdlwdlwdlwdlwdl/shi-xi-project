package com.aliyun.gts.gmall.manager.front.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 降级操作相关配置
 *
 * @author tiansong
 */
@Component
@Data
@RefreshScope
public class DegradationConfig {
    /**
     * 是否过滤空值字段
     */
    @Value(value = "${degradation.filter.filed:true}")
    private boolean filterFiled;

    /**
     * 使用禁止使用参数作为登录
     */
    @Value(value = "${degradation.login.force:false}")
    private boolean forceLogin;

    /**
     * 是否打开cache
     */
    @Value(value = "${degradation.perf.openCache:true}")
    private boolean openCache;
}