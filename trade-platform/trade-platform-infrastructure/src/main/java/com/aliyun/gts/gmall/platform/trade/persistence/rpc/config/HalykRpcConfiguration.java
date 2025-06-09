package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.DeliveryFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.SynDeliveryMsgFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.UcsMsgFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
public class HalykRpcConfiguration {

    @Value("${dubbo.consumer.halykDirectUrl:}")
    private String halykDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public SynDeliveryMsgFacade getSynDeliveryMsgFacade() {
        return this.serviceSubscriber.consumer(SynDeliveryMsgFacade.class).directUrl(halykDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public DeliveryFacade getDeliveryFacade() {
        return this.serviceSubscriber.consumer(DeliveryFacade.class).directUrl(halykDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public UcsMsgFacade getUcsMsgFacade() {
        return this.serviceSubscriber.consumer(UcsMsgFacade.class).directUrl(halykDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public EPaymentFacade getEPaymentFacade() {
        return this.serviceSubscriber.consumer(EPaymentFacade.class).directUrl(halykDirectUrl).subscribe();
    }
}
