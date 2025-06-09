package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;


import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.integration.api.facade.ShopInvoiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 开放平台服务
 * @author admin
 */
@Configuration
public class IntegrationApiRpcConfiguration {

    @Value("${dubbo.consumer.integrationDirectUrl:}")
    private String integrationDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

//    @Bean
//    @ConditionalOnMissingBean
//    public ShopInvoiceFacade getShopInvoiceFacade() {
//        return this.serviceSubscriber.consumer(ShopInvoiceFacade.class).directUrl(integrationDirectUrl).subscribe();
//    }

}
