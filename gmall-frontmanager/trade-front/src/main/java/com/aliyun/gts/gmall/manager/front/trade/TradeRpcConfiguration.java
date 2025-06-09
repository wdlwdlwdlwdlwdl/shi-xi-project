package com.aliyun.gts.gmall.manager.front.trade;

import com.aliyun.gts.gmall.center.pay.api.facade.SettleReadFacade;
import com.aliyun.gts.gmall.center.trade.api.facade.*;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.ApiPayFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.DeliveryFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcLogisticsReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcOrderOperateFlowReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.UserPickLogisticsWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.evaluation.EvaluationWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CityFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交易中心依赖的接口定义
 *
 * @author tiansong
 */
@Configuration
@ConfigurationProperties
public class TradeRpcConfiguration {
    @Value("${dubbo.consumer.tradeDirectUrl:}")
    private String TRADE_URL;

    @Value("${dubbo.consumer.payDirectUrl:}")
    private String PAY_URL;

    @Value("${dubbo.consumer.searchDirectUrl:}")
    private String SEARCH_URL;

    @Value("${dubbo.consumer.customerDirectUrl:}")
    private String CUSTOMER_URL;

    @Value("${dubbo.consumer.creditDirectUrl:}")
    private String CREDIT_URL;

    @Value("${dubbo.consumer.halykCustomizedDirectUrl:}")
    private String HALYK_CUSTOMIZED_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;

    @Value("${dubbo.consumer.itemDirectUrl:}")
    private String itemDirectUrl;

    @Bean
    @ConditionalOnMissingBean
    public CommercialReadFacade getCommercialReadFacade() {
        return this.serviceSubscriber.consumer(CommercialReadFacade.class).directUrl(itemDirectUrl).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CartReadFacade cartReadFacade() {
        return serviceSubscriber.consumer(CartReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CartWriteFacade cartWriteFacade() {
        return serviceSubscriber.consumer(CartWriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderReadFacade orderReadFacade() {
        return serviceSubscriber.consumer(OrderReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    EvoucherFacade evoucherFacade() {
        return serviceSubscriber.consumer(EvoucherFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderWriteFacade orderWriteFacade() {
        return serviceSubscriber.consumer(OrderWriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderPayReadFacade orderPayReadFacade() {
        return serviceSubscriber.consumer(OrderPayReadFacade.class).directUrl(TRADE_URL).subscribe();
    }
//    @Bean
//    @ConditionalOnMissingBean
//    OrderPayReadFacade orderQueryAbility() {
//        return serviceSubscriber.consumer(OrderQueryAbility.class).directUrl(TRADE_URL).subscribe();
//    }
    @Bean
    @ConditionalOnMissingBean
    com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade  payReadFacade() {
        return serviceSubscriber.consumer(com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade.class).directUrl(PAY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderExtFacade orderExtFacade() {
        return serviceSubscriber.consumer(OrderExtFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SettleReadFacade settleReadFacade() {
        return serviceSubscriber.consumer(SettleReadFacade.class).directUrl(PAY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    OrderPayWriteFacade orderPayWriteFacade() {
        return serviceSubscriber.consumer(OrderPayWriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    TcLogisticsReadFacade tcLogisticsReadFacade() {
        return serviceSubscriber.consumer(TcLogisticsReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    EvaluationWriteFacade evaluationWriteFacade() {
        return serviceSubscriber.consumer(EvaluationWriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    EvaluationReadFacade evaluationReadFacade() {
        return serviceSubscriber.consumer(EvaluationReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ItemReadFacade itemCacheReadFacade() {
        return this.serviceSubscriber.consumer(ItemReadFacade.class).directUrl(SEARCH_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ReversalReadFacade reversalReadFacade() {
        return this.serviceSubscriber.consumer(ReversalReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ReversalWriteFacade reversalWriteFacade() {
        return this.serviceSubscriber.consumer(ReversalWriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerReadFacade customerReadFacade() {
        return this.serviceSubscriber.consumer(CustomerReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CustomerAddressReadFacade customerAddressReadFacade() {
        return this.serviceSubscriber.consumer(CustomerAddressReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    SellerReadFacade sellerReadFacade() {
        return this.serviceSubscriber.consumer(SellerReadFacade.class).directUrl(CUSTOMER_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PayCATwriteFacade getPayCATwriteFacade() {
        return this.serviceSubscriber.consumer(PayCATwriteFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    PromotionBuyOrderFacade getPromotionBuyOrderFacade() {
        return this.serviceSubscriber.consumer(PromotionBuyOrderFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    InvoiceFacade invoiceFacade() {
        return serviceSubscriber.consumer(InvoiceFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    EPaymentFacade epaymentFacade() {
        return serviceSubscriber.consumer(EPaymentFacade.class).directUrl(HALYK_CUSTOMIZED_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    ApiPayFacade apiPayFacade() {
        return serviceSubscriber.consumer(ApiPayFacade.class).directUrl(HALYK_CUSTOMIZED_URL).subscribe();
    }
    @Bean
    @ConditionalOnMissingBean
    CityFacade cityFacade() {
        return serviceSubscriber.consumer(CityFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    CancelReasonFacade cancelReasonFacade() {
        return serviceSubscriber.consumer(CancelReasonFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    DeliveryFacade deliveryFacade() {
        return serviceSubscriber.consumer(DeliveryFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    TcOrderOperateFlowReadFacade tcOrderOperateFlowReadFacade() {
        return serviceSubscriber.consumer(TcOrderOperateFlowReadFacade.class).directUrl(TRADE_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade payWriteFacade() {
        return serviceSubscriber.consumer(com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade.class).directUrl(PAY_URL).subscribe();
    }

    @Bean
    @ConditionalOnMissingBean
    UserPickLogisticsWriteFacade getUserPickLogisticsWriteFacade() {
        return serviceSubscriber.consumer(UserPickLogisticsWriteFacade.class).directUrl(PAY_URL).subscribe();
    }

}
