package com.aliyun.gts.gmall.manager.front.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.center.user.api.facade.CheckReceivePointReadFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CheckReceivePointWriteFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerBindThirdAccountFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerLogOffFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerSmsFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.CommonHttpRequestFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerPhoneVerifyFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;

/**
 * dubbo 服务的订阅
 *
 * @author tiansong
 */
@Configuration
@ConfigurationProperties
public class LoginRpcConfiguration {

    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    CustomerReadFacade customerReadFacade() {
        return this.serviceSubscriber.consumer(CustomerReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerWriteFacade customerWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerPhoneVerifyFacade customerPhoneVerifyFacade() {
        return this.serviceSubscriber.consumer(CustomerPhoneVerifyFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }


    @Bean
    @ConditionalOnMissingBean
    CustomerBindThirdAccountFacade customerBindThirdAccountFacade() {
        return this.serviceSubscriber.consumer(CustomerBindThirdAccountFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerLogOffFacade customerLogOffFacade() {
        return this.serviceSubscriber.consumer(CustomerLogOffFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CheckReceivePointWriteFacade checkReceivePointWriteFacade() {
        return this.serviceSubscriber.consumer(CheckReceivePointWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CheckReceivePointReadFacade checkReceivePointReadFacade() {
        return this.serviceSubscriber.consumer(CheckReceivePointReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerSmsFacade customerSmsFacade() {
        return this.serviceSubscriber.consumer(CustomerSmsFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CommonHttpRequestFacade commonHttpRequestFacade() {
        return this.serviceSubscriber.consumer(CommonHttpRequestFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }
}
