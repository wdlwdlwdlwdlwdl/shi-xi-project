package com.aliyun.gts.gmall.manager.front.trade;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties
public class ContractRpcConfiguration {
    @Value("${dubbo.consumer.contract:}")
    private String contractUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

/*    @Bean
    @ConditionalOnMissingBean
    public ContractFacade contractFacade() {
        return serviceSubscriber.consumer(ContractFacade.class).directUrl(contractUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public B2BContractFacade b2BContractFacade() {
        return serviceSubscriber.consumer(B2BContractFacade.class).directUrl(contractUrl).subscribe();
    }*/
}