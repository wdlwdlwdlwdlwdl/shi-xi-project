package com.aliyun.gts.gmall.manager.front.sourcing.config;

import com.aliyun.gts.gmall.manager.front.b2bcomm.configuration.DubboConfiguration;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gshine
 * @since 2/22/21 11:24 AM
 */
@Configuration
public class MaterialConfiguration extends DubboConfiguration {

    //如果配置了directUrl则使用指定地址，否则使用edas服务
    @Value("${dubbo.directUrl.material:}")
    private String directUrl;

    @Bean
    @ConditionalOnMissingBean
    public CategoryReadFacade categoryReadFacade() {
        return consumer(CategoryReadFacade.class, directUrl);
    }
}
