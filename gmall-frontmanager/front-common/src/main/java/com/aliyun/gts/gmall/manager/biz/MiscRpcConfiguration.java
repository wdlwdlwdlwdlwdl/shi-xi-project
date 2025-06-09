package com.aliyun.gts.gmall.manager.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.center.misc.api.facade.city.CityReadFacade;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictReadFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;

@Configuration
@ConfigurationProperties
public class MiscRpcConfiguration {
    @Value("${dubbo.misc.miscDirectUrl:}")
    private String miscUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    DictReadFacade getDictReadFacade() {
        return this.serviceSubscriber.consumer(DictReadFacade.class).directUrl(miscUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CityReadFacade cityReadFacade() {
        return this.serviceSubscriber.consumer(CityReadFacade.class).directUrl(miscUrl).subscribe();
    }
}
