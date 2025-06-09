package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.meta.api.facade.MetaExtendReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaExtendRpcConfiguration {

    @Value("${dubbo.consumer.metaExtendDirectUrl:}")
    private String metaExtendDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public MetaExtendReadFacade getMetaExtendReadFacade() {
        return this.serviceSubscriber.consumer(MetaExtendReadFacade.class).directUrl(metaExtendDirectUrl).subscribe();
    }

}
