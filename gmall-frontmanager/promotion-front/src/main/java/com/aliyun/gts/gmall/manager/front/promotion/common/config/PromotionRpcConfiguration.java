package com.aliyun.gts.gmall.manager.front.promotion.common.config;

import com.aliyun.gts.gmall.center.promotion.api.facade.*;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class PromotionRpcConfiguration {
    @Value("${dubbo.consumer.promotionDirectUrl:}")
    private String directUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    PlayActivityReadFacade playActivityReadFacade() {
        return serviceSubscriber.consumer(PlayActivityReadFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ShareFissionWriteFacade shareFissionWriteFacade() {
        return serviceSubscriber.consumer(ShareFissionWriteFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ShareFissionReadFacade shareFissionReadFacade() {
        return serviceSubscriber.consumer(ShareFissionReadFacade.class).directUrl(directUrl).subscribe();
    }

    @ConditionalOnMissingBean
    @Bean
    PlayAwardFacade playAwardFacade() {
        return serviceSubscriber.consumer(PlayAwardFacade.class).directUrl(directUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromotionConfigFacade promotionConfigFacade() {
        return serviceSubscriber.consumer(PromotionConfigFacade.class).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PointLotteryFlowReadFacade pointLotteryFlowReadFacade() {
        return serviceSubscriber.consumer(PointLotteryFlowReadFacade.class).subscribe();
    }


    /**
     * 新增分组rpc接口配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    GrBizGroupWriteFacade grBizGroupWriteFacade() {
        return serviceSubscriber.consumer(GrBizGroupWriteFacade.class).subscribe();
    }
}
