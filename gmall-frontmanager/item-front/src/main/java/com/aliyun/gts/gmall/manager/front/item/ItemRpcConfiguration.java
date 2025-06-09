package com.aliyun.gts.gmall.manager.front.item;

import com.aliyun.gts.gmall.platform.open.customized.api.facade.GoogleFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.center.item.api.facade.AgreementPriceFacade;
import com.aliyun.gts.gmall.center.item.api.facade.PointItemQueryFacade;
import com.aliyun.gts.gmall.center.misc.api.facade.region.RegionReadFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.manager.front.item.convertor.RegionConverter;
import com.aliyun.gts.gmall.platform.item.api.facade.brand.BrandReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryPropGroupReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.delivery.SellerSelfDeliveryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemWriteFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.property.PropertyReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.SkuQuoteReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.warehouse.WarehouseReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import com.aliyun.gts.gmall.searcher.api.facade.EvaluationQueryFacade;
import com.aliyun.gts.gmall.searcher.api.facade.ItemQueryFacade;
import com.aliyun.gts.gmall.searcher.api.facade.SearchThirdFacade;

/**
 * dubbo 服务的订阅
 *
 * @author tiansong
 */
@Configuration
@ConfigurationProperties
public class ItemRpcConfiguration {
    @Value("${dubbo.consumer.itemDirectUrl:}")
    private String ITEM_URL;
    @Value("${dubbo.consumer.searchDirectUrl:}")
    private String SEARCH_URL;
    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;
    @Value("${dubbo.consumer.promotionDirectUrl:}")
    private String PROMOTION_URL;
    @Value("${dubbo.consumer.tradeDirectUrl:}")
    private String TRADE_URL;
    @Value("${dubbo.consumer.miscDirectUrl:}")
    private String MISC_URL;
    @Value("${dubbo.consumer.categoryDirectUrl:}")
    private String CATEGORY_URL;
    @Value("${dubbo.consumer.userDirectUrl:}")
    private String USER_URL;
    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    EvaluationQueryFacade evaluationQueryFacade() {
        return this.serviceSubscriber.consumer(EvaluationQueryFacade.class).directUrl(SEARCH_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ItemQueryFacade itemQueryFacade() {
        return this.serviceSubscriber.consumer(ItemQueryFacade.class).directUrl(SEARCH_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerReadFacade customerReadFacade() {
        return this.serviceSubscriber.consumer(CustomerReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromotionReadFacade promotionReadFacade() {
        return this.serviceSubscriber.consumer(PromotionReadFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CartReadFacade cartReadFacade() {
        return serviceSubscriber.consumer(CartReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromotionCouponFacade promotionCouponFacade() {
        return serviceSubscriber.consumer(PromotionCouponFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    AccountBookReadFacade accountBookReadFacade() {
        return serviceSubscriber.consumer(AccountBookReadFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    AccountBookWriteFacade accountBookWriteFacade() {
        return serviceSubscriber.consumer(AccountBookWriteFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    RegionReadFacade regionReadFacade() {
        return serviceSubscriber.consumer(RegionReadFacade.class).directUrl(MISC_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    RegionConverter regionConverter() {
        return serviceSubscriber.consumer(RegionConverter.class).directUrl(MISC_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ItemWriteFacade itemWriteFacade() {
        return serviceSubscriber.consumer(ItemWriteFacade.class).directUrl(ITEM_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ItemReadFacade itemReadFacade() {
        return serviceSubscriber.consumer(ItemReadFacade.class).directUrl(ITEM_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    AgreementPriceFacade agreementPriceFacade() {
        return serviceSubscriber.consumer(AgreementPriceFacade.class).directUrl(ITEM_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    GrBizGroupReadFacade grBizGroupReadFacade() {
        return serviceSubscriber.consumer(GrBizGroupReadFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PointItemQueryFacade pointItemQueryFacade() {
        return serviceSubscriber.consumer(PointItemQueryFacade.class).directUrl(ITEM_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CategoryReadFacade categoryReadFacade() {
        return serviceSubscriber.consumer(CategoryReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CommercialReadFacade commercialReadFacade() {
        return serviceSubscriber.consumer(CommercialReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SellerSelfDeliveryReadFacade sellerSelfDeliveryReadFacade() {
        return serviceSubscriber.consumer(SellerSelfDeliveryReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    WarehouseReadFacade warehouseReadFacade() {
        return serviceSubscriber.consumer(WarehouseReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SellerReadFacade sellerReadFacade() {
        return serviceSubscriber.consumer(SellerReadFacade.class).directUrl(USER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CategoryPropGroupReadFacade categoryPropGroupReadFacade() {
        return serviceSubscriber.consumer(CategoryPropGroupReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    BrandReadFacade brandReadFacade() {
        return serviceSubscriber.consumer(BrandReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PropertyReadFacade propertyReadFacade() {
        return serviceSubscriber.consumer(PropertyReadFacade.class).directUrl(CATEGORY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SearchThirdFacade searchThirdFacade1() {
        return serviceSubscriber.consumer(SearchThirdFacade.class).directUrl(SEARCH_URL).subscribe();
    }
    
    @Bean
    @ConditionalOnMissingBean
    SkuQuoteReadFacade skuQuoteReadFacade() {
        return serviceSubscriber.consumer(SkuQuoteReadFacade.class).directUrl(SEARCH_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    GoogleFacade googleFacade() {
        return serviceSubscriber.consumer(GoogleFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }
}
