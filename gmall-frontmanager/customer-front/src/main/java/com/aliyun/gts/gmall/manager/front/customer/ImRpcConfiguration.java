package com.aliyun.gts.gmall.manager.front.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImMessageReadFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImNoticeFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImOpenLoginFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImTemplateFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImUserReadFacade;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 14:00
 */
@Configuration
@ConfigurationProperties
public class ImRpcConfiguration {
    @Value("${dubbo.consumer.imDirectUrl:}")
    private String directUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    ImOpenLoginFacade imOpenLoginFacade() {
        return this.serviceSubscriber.consumer(ImOpenLoginFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ImUserReadFacade imUserReadFacade() {
        return this.serviceSubscriber.consumer(ImUserReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ImMessageReadFacade imMessageReadFacade() {
        return this.serviceSubscriber.consumer(ImMessageReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ImNoticeFacade imNoticeFacade() {
        return this.serviceSubscriber.consumer(ImNoticeFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ImTemplateFacade imTemplateFacade() {
        return this.serviceSubscriber.consumer(ImTemplateFacade.class).directUrl(directUrl).subscribe();
    }
}
