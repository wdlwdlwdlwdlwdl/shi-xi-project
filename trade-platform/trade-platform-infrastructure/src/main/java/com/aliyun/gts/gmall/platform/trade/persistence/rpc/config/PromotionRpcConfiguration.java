package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionAssetsWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 营销服务
 */
@Configuration
public class PromotionRpcConfiguration {

    @Value("${dubbo.consumer.promotionDirectUrl:}")
    private String promotionDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public PromotionAssetsWriteFacade getPromotionAssetsWriteFacade() {
        return this.serviceSubscriber.consumer(PromotionAssetsWriteFacade.class).directUrl(promotionDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public PromotionReadFacade getPromotionReadFacade() {
        return this.serviceSubscriber.consumer(PromotionReadFacade.class).directUrl(promotionDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public PromotionConfigFacade getPromotionConfigFacade() {
        return this.serviceSubscriber.consumer(PromotionConfigFacade.class).directUrl(promotionDirectUrl).subscribe();
    }
}
