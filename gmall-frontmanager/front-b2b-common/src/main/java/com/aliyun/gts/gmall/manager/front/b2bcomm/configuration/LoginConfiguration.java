package com.aliyun.gts.gmall.manager.front.b2bcomm.configuration;

import org.springframework.context.annotation.Configuration;

/**
 * @author 俊贤
 * @date 2021/02/18
 */
@Configuration
public class LoginConfiguration extends DubboConfiguration {
    //如果配置了directUrl则使用指定地址，否则使用edas服务
//    @Value("${dubbo.directUrl.user:}")
//    private String directUrl;
//
//    @Bean
//    @ConditionalOnMissingBean
//    public AccountReadFacade accountReadFacade () {
//        return consumer(AccountReadFacade.class, directUrl);
//    }
}