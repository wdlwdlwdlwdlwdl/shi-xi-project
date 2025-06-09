/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2021 All Rights Reserved.
 */
package com.aliyun.gts.gmall.manager.front;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.aliyun.gts.gmall.middleware.excel.starter.ExcelLoadAutoConfiguration;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * manager bootstrap
 *
 * @author auto-generator
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    ExcelLoadAutoConfiguration.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
@EnableWebMvc
@EnableCreateCacheAnnotation
@ComponentScan(basePackages = {
        "com.aliyun.gts.gmall.searcher.common",
        "com.aliyun.gts.gmall.manager.front",
        "com.aliyun.gts.gmall.framework",
        "com.aliyun.gts.gmall.middleware.cache",
        "com.aliyun.gts.gmall.middleware.minio",
        "com.aliyun.gts.gmall.middleware.mq.ons",
        "com.aliyun.gts.gmall.manager.biz",
        "com.aliyun.gts.gmall.manager.aop",
        "com.aliyun.gts.gmall.middleware.minio",
        "com.aliyun.gts.gmall.platform.gim.api.client"
})
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