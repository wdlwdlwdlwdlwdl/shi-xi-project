package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.center.item.api.facade.PointCreditMkaReadFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImNoticeFacade;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImTemplateFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通知消息服务
 */
@Configuration
public class ImRpcConfiguration {

    @Value("${dubbo.consumer.imDirectUrl:}")
    private String imDirectUrl;

    @Value("${dubbo.consumer.fixedPointDirectUrl:}")
    private String fixedPointDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public ImNoticeFacade getImNoticeFacade() {
        return this.serviceSubscriber.consumer(ImNoticeFacade.class).directUrl(imDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ImTemplateFacade getImTemplateFacade() {
        return this.serviceSubscriber.consumer(ImTemplateFacade.class).directUrl(imDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public PointCreditMkaReadFacade getPointCreditMkaReadFacade() {
        return this.serviceSubscriber.consumer(PointCreditMkaReadFacade.class).directUrl(fixedPointDirectUrl).subscribe();
    }
}
