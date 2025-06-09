package com.aliyun.gts.gmall.manager.front.sourcing.config;

import com.aliyun.gts.gcai.platform.contract.api.facade.ContractFacade;
import com.aliyun.gts.gcai.platform.contract.api.facade.ContractTemplateFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.*;
import com.aliyun.gts.gmall.manager.front.b2bcomm.configuration.DubboConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/1/18 11:48
 */

@Configuration
public class SourcingApiConfiguration extends DubboConfiguration {
    //如果配置了directUrl则使用指定地址，否则使用edas服务
    @Value("${dubbo.directUrl.sourcing:}")
    private String directUrl;

    @Value("${dubbo.directUrl.contract:}")
    private String contractUrl;

    @Bean
    @ConditionalOnMissingBean
    public SourcingReadFacade sourcingReadFacade() {
        return consumer(SourcingReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public SourcingWriteFacade sourcingWriteFacade() {
        return consumer(SourcingWriteFacade.class, directUrl);
    }


    @Bean
    @ConditionalOnMissingBean
    public QuotePriceReadFacade quotePriceReadFacade() {
        return consumer(QuotePriceReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public QuotePriceWriteFacade quotePriceWriteFacade() {
        return consumer(QuotePriceWriteFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public PricingBillReadFacade pricingBillReadFacade() {
        return consumer(PricingBillReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public PricingBillWriteFacade pricingBillWriteFacade() {
        return consumer(PricingBillWriteFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContractTemplateFacade contractTemplateFacade() {
        return consumer(ContractTemplateFacade.class, contractUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public SourcingContractFacade sourcingContractFacade() {
        return consumer(SourcingContractFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContractFacade contractFacade() {
        return consumer(ContractFacade.class, contractUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public SourcingApplyWriteFacade sourcingApplyWriteFacade() {
        return consumer(SourcingApplyWriteFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public SourcingApplyReadFacade sourcingApplyReadFacade() {
        return consumer(SourcingApplyReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public PurchaseRequestFacade purchaseRequestFacade() {
        return consumer(PurchaseRequestFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public BidingPriceWriteFacade bidingPriceWriteFacade() {
        return consumer(BidingPriceWriteFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public BidingPriceReadFacade bidingPriceReadFacade() {
        return consumer(BidingPriceReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    public BidChosingFacade bidChosingFacade() {
        return consumer(BidChosingFacade.class, directUrl);
    }

}