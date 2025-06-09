package com.aliyun.gts.gmall.manager.framework.common;

import org.slf4j.Logger;
import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import lombok.Builder;
import lombok.Getter;

/**
 * 
 * @Title: DubboBuilder.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年11月13日 22:34:01
 * @version V1.0
 */
@Builder
@Getter
public class DubboBuilder {
    private Boolean strong;
    private Logger logger;
    private ResponseCode sysCode;
    private String appName;
    private DatasourceConfig datasourceConfig;

    public DubboDataSource create() {
        return new DubboDataSource(this);
    }

    public DubboDataSource create(DatasourceConfig datasourceConfig) {
        this.datasourceConfig = datasourceConfig;
        return this.create();
    }
}
