package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户相关服务
 */
@Configuration
public class UserRpcConfiguration {

    @Value("${dubbo.consumer.userDirectUrl:}")
    private String userDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public SellerReadFacade getSellerReadFacade() {
        return this.serviceSubscriber.consumer(SellerReadFacade.class).directUrl(userDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomerReadFacade getCustomerReadFacade() {
        return this.serviceSubscriber.consumer(CustomerReadFacade.class).directUrl(userDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomerAddressReadFacade getCustomerAddressReadFacade() {
        return this.serviceSubscriber.consumer(CustomerAddressReadFacade.class).directUrl(userDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public SellerAddressReadFacade getSellerAddressReadFacade() {
        return this.serviceSubscriber.consumer(SellerAddressReadFacade.class).directUrl(userDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public OperatorReadFacade getOperatorReadFacade() {
        return this.serviceSubscriber.consumer(OperatorReadFacade.class).directUrl(userDirectUrl).subscribe();
    }

}
