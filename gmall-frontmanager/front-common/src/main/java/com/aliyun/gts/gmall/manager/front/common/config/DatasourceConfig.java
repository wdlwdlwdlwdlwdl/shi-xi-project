package com.aliyun.gts.gmall.manager.front.common.config;

import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * 降级操作相关配置
 *
 * @author tiansong
 */
@Component
@Data
public class DatasourceConfig {

    @Value(value = "${datasource.error.throwRpcCode:true}")
    private Boolean throwRpcCode;

    /**
     * 性能压测时，针对性能不满足的数据源进行降级，直接失败掉
     */
    @Value(value = "${datasource.perf.dsIds:}")
    private String perfDsIds;

    public Set<String> getPerfDsIdSet() {
        if (StringUtils.isBlank(perfDsIds)) {
            return Collections.emptySet();
        }
        return Sets.newHashSet(perfDsIds.split(","));
    }
}