package com.aliyun.gts.gmall.manager.front.customer;

import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.CustomerCampSubscribeReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.CustomerCampSubscribeWriteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelReadFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustShopInterestRelWriteFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerExternalFacade;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.center.user.api.facade.SellerIndicatorReadFacade;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.operator.api.facade.OperatorReadFacade;
import com.aliyun.gts.gmall.platform.operator.api.facade.RoleReadFacade;
import com.aliyun.gts.gmall.platform.operator.api.facade.SceneReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromCampaignReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerFavouriteReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerFavouriteWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerItemVisitHisReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerSellerRelationFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerAddressReadFacade;

/**
 * dubbo 服务的订阅
 *
 * @author tiansong
 */
@Configuration
public class CustomerRpcConfiguration {
    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;
    @Value("${dubbo.consumer.promotionDirectUrl:}")
    private String PROMOTION_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Bean
    @ConditionalOnMissingBean
    CustomerReadFacade customerReadFacade() {
        return this.serviceSubscriber.consumer(CustomerReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerWriteFacade customerWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerAddressReadFacade customerAddressReadFacade() {
        return this.serviceSubscriber.consumer(CustomerAddressReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerAddressWriteFacade customerAddressWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerAddressWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SellerAddressReadFacade sellerAddressReadFacade() {
        return this.serviceSubscriber.consumer(SellerAddressReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromotionCouponFacade promotionCouponFacade() {
        return this.serviceSubscriber.consumer(PromotionCouponFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerInvoiceReadFacade customerInvoiceReadFacade() {
        return this.serviceSubscriber.consumer(CustomerInvoiceReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerInvoiceWriteFacade customerInvoiceWriteFacade() {
        return this.serviceSubscriber.consumer(CustomerInvoiceWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OperatorReadFacade operatorReadFacade() {
        return this.serviceSubscriber.consumer(OperatorReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SceneReadFacade sceneReadFacade() {
        return this.serviceSubscriber.consumer(SceneReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    RoleReadFacade roleReadFacade() {
        return this.serviceSubscriber.consumer(RoleReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromCampaignReadFacade promCampaignReadFacade() {
        return this.serviceSubscriber.consumer(PromCampaignReadFacade.class).directUrl(PROMOTION_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerExternalFacade customerExternalFacade() {
        return this.serviceSubscriber.consumer(CustomerExternalFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerReadExtFacade customerReadExtFacade() {
        return this.serviceSubscriber.consumer(CustomerReadExtFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerSellerRelationFacade customerSellerRelationFacade() {
        return this.serviceSubscriber.consumer(CustomerSellerRelationFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SellerIndicatorReadFacade sellerIndicatorReadFacade() {
        return this.serviceSubscriber.consumer(SellerIndicatorReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustShopInterestRelWriteFacade getCustShopInterestRelWriteFacade() {
        return serviceSubscriber.consumer(CustShopInterestRelWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustShopInterestRelReadFacade getCustShopInterestRelReadFacade() {
        return serviceSubscriber.consumer(CustShopInterestRelReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerFavouriteReadFacade getCustomerFavouriteReadFacade() {
        return serviceSubscriber.consumer(CustomerFavouriteReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerFavouriteWriteFacade getCustomerFavouriteWriteFacade() {
        return serviceSubscriber.consumer(CustomerFavouriteWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }


    @Bean
    @ConditionalOnMissingBean
    CustomerItemVisitHisReadFacade getCustomerItemVisitHisReadFacade() {
        return serviceSubscriber.consumer(CustomerItemVisitHisReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerBankCardInfoReadFacade customerBankCardInfoReadFacade() {
        return serviceSubscriber.consumer(CustomerBankCardInfoReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerBankCardInfoWriteFacade customerBankCardInfoWriteFacade() {
        return serviceSubscriber.consumer(CustomerBankCardInfoWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }



    @Bean
    @ConditionalOnMissingBean
    CustomerCampSubscribeWriteFacade customerCampSubscribeWriteFacade() {
        return serviceSubscriber.consumer(CustomerCampSubscribeWriteFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }


    @Bean
    @ConditionalOnMissingBean
    CustomerCampSubscribeReadFacade customerCampSubscribeReadFacade() {
        return serviceSubscriber.consumer(CustomerCampSubscribeReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

}
