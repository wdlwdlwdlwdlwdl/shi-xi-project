package com.aliyun.gts.gmall.manager.front.media.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo 服务的订阅
 *
 * @author tiansong
 */
@Configuration
public class MediaCustomerApiConfiguration {
    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;


    @Bean
    @ConditionalOnMissingBean
    public OperatorReadFacade operatorReadFacade() {
        return this.serviceSubscriber.consumer(OperatorReadFacade.class).directUrl(CUSTOMER_URL)
                .subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public SellerReadFacade getSellerReadFacade() {
        return this.serviceSubscriber.consumer(SellerReadFacade.class).directUrl(CUSTOMER_URL)
                .subscribe();
    }
}