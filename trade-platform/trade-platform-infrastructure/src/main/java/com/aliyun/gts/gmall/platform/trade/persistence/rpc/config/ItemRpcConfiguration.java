package com.aliyun.gts.gmall.platform.trade.persistence.rpc.config;

import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryCommissionReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.inventory.InventoryWriteFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemWriteFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.OutModelChangeNoticeWriteFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.SkuQuoteReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 商品服务
 */
@Configuration
public class ItemRpcConfiguration {

    @Value("${dubbo.consumer.itemDirectUrl:}")
    private String itemDirectUrl;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    public ItemReadFacade getItemReadFacade() {
        return this.serviceSubscriber.consumer(ItemReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean

    public InventoryWriteFacade getInventoryWriteFacade() {
        return this.serviceSubscriber.consumer(InventoryWriteFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CategoryReadFacade getCategoryReadFacade() {
        return this.serviceSubscriber.consumer(CategoryReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkuQuoteReadFacade getSkuQuoteReadFacade() {
        return this.serviceSubscriber.consumer(SkuQuoteReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommercialReadFacade getCommercialReadFacade() {
        return this.serviceSubscriber.consumer(CommercialReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public CategoryCommissionReadFacade getCategoryCommissionReadFacade() {
        return this.serviceSubscriber.consumer(CategoryCommissionReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public ItemWriteFacade getItemWriteFacade() {
        return this.serviceSubscriber.consumer(ItemWriteFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    public OutModelChangeNoticeWriteFacade outModelChangeNoticeWriteFacade(){
        return this.serviceSubscriber.consumer(OutModelChangeNoticeWriteFacade.class).directUrl(itemDirectUrl).subscribe();
    }

}
