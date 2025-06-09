/**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2021 All Rights Reserved.
 */
package com.aliyun.gts.gmall.platform.bootstrap;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {
                "com.aliyun.gts.gmall.platform.trade",
                "com.aliyun.gts.gmall.framework",
                "com.aliyun.gts.gmall.searcher.common",
                "com.aliyun.gts.gmall.middleware.logistics"
        })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@MapperScan(value={"com.aliyun.gts.gmall.platform.trade.persistence.mapper"})
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
