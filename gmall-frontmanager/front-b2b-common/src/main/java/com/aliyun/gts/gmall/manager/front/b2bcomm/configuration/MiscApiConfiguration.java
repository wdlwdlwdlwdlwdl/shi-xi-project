package com.aliyun.gts.gmall.manager.front.b2bcomm.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictReadFacade;
import com.aliyun.gts.gmall.center.misc.api.facade.dict.DictWriteFacade;
import com.aliyun.gts.gmall.center.misc.api.facade.region.RegionReadFacade;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/22 10:57
 */
@Configuration
public class MiscApiConfiguration extends DubboConfiguration {
    //如果配置了directUrl则使用指定地址，否则使用edas服务
    @Value("${dubbo.directUrl.misc:}")
    private String directUrl;

    @Bean
    @ConditionalOnMissingBean
     DictReadFacade dictReadFacade() {
        return consumer(DictReadFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
     DictWriteFacade dictWriteFacade() {
        return consumer(DictWriteFacade.class, directUrl);
    }

    @Bean
    @ConditionalOnMissingBean
     RegionReadFacade regionReadFacade() {
        return consumer(RegionReadFacade.class, directUrl);
    }

//    @Bean
//    @ConditionalOnMissingBean
//    public FlowEngineFacade flowEngineFacade() {
//        return consumer(FlowEngineFacade.class, directUrl);
//    }

}