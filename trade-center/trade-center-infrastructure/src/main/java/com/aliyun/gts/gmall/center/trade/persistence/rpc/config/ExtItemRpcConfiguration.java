package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.center.item.api.facade.AgreementPriceFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.item.api.facade.delivery.SellerSelfDeliveryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.SkuQuoteReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.warehouse.WarehouseReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通知消息服务
 */
@Configuration
public class ExtItemRpcConfiguration {

    @Value("${dubbo.consumer.itemDirectUrl:}")
    private String itemDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public AgreementPriceFacade getAgreementPriceFacade() {
        return this.serviceSubscriber.consumer(AgreementPriceFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkuQuoteReadFacade getSkuQuoteReadFacade() {
        return this.serviceSubscriber.consumer(SkuQuoteReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public SellerSelfDeliveryReadFacade getSellerSelfDeliveryReadFacade() {
        return this.serviceSubscriber.consumer(SellerSelfDeliveryReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public WarehouseReadFacade getWarehouseReadFacade() {
        return this.serviceSubscriber.consumer(WarehouseReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }


}
