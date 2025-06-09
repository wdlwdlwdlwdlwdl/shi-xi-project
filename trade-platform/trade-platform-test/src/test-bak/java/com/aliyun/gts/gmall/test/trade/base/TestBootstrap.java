package com.aliyun.gts.gmall.test.platform.base;

import com.aliyun.gts.gmall.platform.bootstrap.Bootstrap;
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
                "com.aliyun.gts.gmall.test.trade.pay",
                "com.aliyun.gts.gmall.middleware.logistics",
                "com.aliyun.gts.gmall.test.trade.mock"
        })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@MapperScan(value={"com.aliyun.gts.gmall.platform.trade.persistence.mapper"})
@Slf4j

public class TestBootstrap {

    public static void main(String[] args) {
        try {
            SpringApplication.run(Bootstrap.class, args);
        } catch (Exception e) {
            log.error("service start failed, cause:{}", Throwables.getStackTraceAsString(e));
            throw e;
        }
    }
}
