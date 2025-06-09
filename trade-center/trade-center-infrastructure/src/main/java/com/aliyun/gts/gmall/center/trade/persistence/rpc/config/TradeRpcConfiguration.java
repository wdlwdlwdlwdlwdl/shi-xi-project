package com.aliyun.gts.gmall.center.trade.persistence.rpc.config;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.consumer.ServiceSubscriber;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.pay.api.facade.TcConfirmPayFacade;
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

    @Value("${dubbo.consumer.payDirectUrl:}")
    private String PAY_URL;

    @Value("${dubbo.consumer.halykCustomizedDirectUrl:}")
    private String HALYK_CUSTOMIZED_URL;

    @Autowired
    private ServiceSubscriber serviceSubscriber;


    @Bean
    @ConditionalOnMissingBean
    public TcConfirmPayFacade getTcConfirmPayFacade() {
        return serviceSubscriber.consumer(TcConfirmPayFacade.class).directUrl(PAY_URL).subscribe();
    }


    @Bean
    @ConditionalOnMissingBean
    public EPaymentFacade epaymentFacade() {
        return serviceSubscriber.consumer(EPaymentFacade.class).directUrl(HALYK_CUSTOMIZED_URL).subscribe();
    }

}