package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImManualFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.UcsSendFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息服务
 */
@Configuration
public class GimRpcConfiguration {

    @Value("${dubbo.consumer.gimDirectUrl:}")
    private String gimDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public UcsSendFacade getUcsSendFacade() {
        return this.serviceSubscriber.consumer(UcsSendFacade.class).directUrl(gimDirectUrl).subscribe();
    }
    @Bean
    @ConditionalOnMissingBean
    public ImManualFacade getImManualFacade() {
        return this.serviceSubscriber.consumer(ImManualFacade.class).directUrl(gimDirectUrl).subscribe();
    }
}
