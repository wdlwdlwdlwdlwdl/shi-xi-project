package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.center.promotion.api.facade.PromotionManZengFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/12 10:50
 */
@Configuration
public class PromotionApiRpcConfiguration {
    @Value("${dubbo.consumer.promotionDirectUrl:}")
    private String directUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public PromotionManZengFacade promotionManZengFacade() {
        return this.serviceSubscriber.consumer(PromotionManZengFacade.class).directUrl(directUrl).subscribe();
    }


    @Bean
    @ConditionalOnMissingBean
    public CustomerInvoiceReadFacade getCustomerInvoiceReadFacade() {
        return this.serviceSubscriber.consumer(CustomerInvoiceReadFacade.class).directUrl(directUrl).subscribe();
    }
}
