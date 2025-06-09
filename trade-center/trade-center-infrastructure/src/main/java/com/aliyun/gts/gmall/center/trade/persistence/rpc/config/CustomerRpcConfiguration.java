package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通知消息服务
 */
@Configuration
public class CustomerRpcConfiguration {

    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public CustomerWriteFacade customerWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }
    @Bean
    @ConditionalOnMissingBean
    public CustomerBankCardInfoReadFacade customerBankCardInfoReadFacade() {
        return this.serviceSubscriber.consumer(CustomerBankCardInfoReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }
    @Bean
    @ConditionalOnMissingBean
    public CustomerBankCardInfoWriteFacade customerBankCardInfoWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerBankCardInfoWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomerReadExtFacade customerReadExtFacade() {
        return this.serviceSubscriber.consumer(CustomerReadExtFacade.class).directUrl(CUSTOMER_URL)
                .subscribe();
    }

}
