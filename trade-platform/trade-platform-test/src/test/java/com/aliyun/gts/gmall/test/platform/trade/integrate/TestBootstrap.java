package com.aliyun.gts.gmall.test.platform.trade.integrate; /**
 * com.aliyun.gts.gmall
 * Copyright(c) 2012-2021 All Rights Reserved.
 */

import com.aliyun.gts.gmall.framework.biz.component.pay.builder.PayGateWayFactoryBuilder;
import com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService;
import com.aliyun.gts.gmall.platform.trade.core.config.GmallOssClientConfiguration;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@MapperScan(value = {"com.aliyun.gts.gmall.platform.trade.persistence.mapper"})
@Slf4j
@ComponentScan(
        basePackages = {
                "com.aliyun.gts.gmall.platform.trade",
                "com.aliyun.gts.gmall.framework",
                "com.aliyun.gts.gmall.searcher.common",
                "com.aliyun.gts.gmall.middleware.logistics",
                "com.aliyun.gts.gmall.test.platform.trade.integrate.mock",
        },
        excludeFilters = {
                @Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                DataSourceAutoConfiguration.class,
                                PayGateWayFactoryBuilder.class,
                                GmallOssClientConfiguration.class,
                        }
                ),
                @Filter(
                        type = FilterType.REGEX,
                        pattern = {
                                // dayu 环境 cannot find symbol: class Bootstrap
                                "com.aliyun.gts.gmall.platform.bootstrap.Bootstrap",
                                // rpc consumer
                                "com.aliyun.gts.gmall.platform.trade.persistence.rpc.config.*",
                                // 不排子类
                                "com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService",
                        }
                )
        })
public class TestBootstrap {

    public static void main(String[] args) throws Exception {
        try {
            SpringApplication.run(TestBootstrap.class, args);
        } catch (Exception e) {
            log.error("service start failed, cause:{}", Throwables.getStackTraceAsString(e));
            throw e;
        }
    }

}
