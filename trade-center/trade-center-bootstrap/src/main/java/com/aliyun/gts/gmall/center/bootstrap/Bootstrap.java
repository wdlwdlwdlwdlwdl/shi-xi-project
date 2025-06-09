/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2021 All Rights Reserved.
 */
package com.aliyun.gts.gmall.center.bootstrap;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class},
    scanBasePackages = {
        "com.aliyun.gts.gmall.platform.trade",
        "com.aliyun.gts.gmall.framework",
        "com.aliyun.gts.gmall.searcher.common",
        "com.aliyun.gts.gmall.center.trade",
        "com.aliyun.gts.gmall.middleware.xxljob",
        "com.aliyun.gts.gmall.middleware.mq",
        "com.aliyun.gts.gmall.middleware.logistics"
    }
)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@MapperScan(
    value = {
        "com.aliyun.gts.gmall.platform.trade.persistence.mapper",
        "com.aliyun.gts.gmall.center.trade.persistence.mapper"
    }
)
@EnableTransactionManagement
@Slf4j
public class Bootstrap {

    public static void main(String[] args) {
        try {
            SpringApplication.run(Bootstrap.class, args);
        } catch (Exception e) {
            log.error("service start failed, cause:{}", Throwables.getStackTraceAsString(e));
            throw e;
        }
    }

}
