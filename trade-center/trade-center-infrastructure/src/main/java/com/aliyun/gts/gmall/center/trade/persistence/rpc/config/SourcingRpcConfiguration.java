package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;

//import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillReadFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillWriteFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceReadFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceWriteFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SourcingRpcConfiguration {

//    @Value("${dubbo.consumer.sourcingDirectUrl:}")
//    private String directUrl;
//
//    @Autowired
//    private ServiceSubscriber serviceSubscriber;
//
//    @Bean
//    @ConditionalOnMissingBean
//    public PricingBillReadFacade pricingBillReadFacade() {
//        return this.serviceSubscriber.consumer(PricingBillReadFacade.class).directUrl(directUrl).subscribe();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public PricingBillWriteFacade pricingBillWriteFacade() {
//        return this.serviceSubscriber.consumer(PricingBillWriteFacade.class).directUrl(directUrl).subscribe();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public QuotePriceReadFacade quotePriceReadFacade() {
//        return this.serviceSubscriber.consumer(QuotePriceReadFacade.class).directUrl(directUrl).subscribe();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public QuotePriceWriteFacade quotePriceWriteFacade() {
//        return this.serviceSubscriber.consumer(QuotePriceWriteFacade.class).directUrl(directUrl).subscribe();
//    }
}
