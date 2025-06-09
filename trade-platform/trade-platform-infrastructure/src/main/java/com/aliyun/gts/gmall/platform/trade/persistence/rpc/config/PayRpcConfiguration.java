package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付服务
 */
@Configuration
public class PayRpcConfiguration {

    @Value("${dubbo.consumer.payDirectUrl:}")
    private String payDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public OrderPayReadFacade getOrderPayReadFacade() {
        return this.serviceSubscriber.consumer(OrderPayReadFacade.class).directUrl(payDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrderPayWriteFacade getOrderPayWriteFacade() {
        return this.serviceSubscriber.consumer(OrderPayWriteFacade.class).directUrl(payDirectUrl).subscribe();
    }

}
