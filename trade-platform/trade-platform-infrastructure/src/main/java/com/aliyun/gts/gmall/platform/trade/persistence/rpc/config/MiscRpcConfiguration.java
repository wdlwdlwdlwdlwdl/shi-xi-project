package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.center.misc.api.facade.city.CityReadFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息服务
 */
@Configuration
public class MiscRpcConfiguration {

    @Value("${dubbo.consumer.miscDirectUrl:}")
    private String miscDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public CityReadFacade getCityReadFacade() {
        return this.serviceSubscriber.consumer(CityReadFacade.class).directUrl(miscDirectUrl).subscribe();
    }

}
