package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 积分服务
 */
@Configuration
public class PointRpcConfiguration {

    @Value("${dubbo.consumer.pointDirectUrl:}")
    private String pointDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public AccountBookReadFacade getAccountBookReadFacade() {
        return this.serviceSubscriber.consumer(AccountBookReadFacade.class).directUrl(pointDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccountBookWriteFacade getAccountBookWriteFacade() {
        return this.serviceSubscriber.consumer(AccountBookWriteFacade.class).directUrl(pointDirectUrl).subscribe();
    }
}
