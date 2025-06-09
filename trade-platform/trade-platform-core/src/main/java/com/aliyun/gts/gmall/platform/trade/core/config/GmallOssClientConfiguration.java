package com.aliyun.gts.gmall.platform.trade.core.config;

import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GmallOssClientConfiguration {

    /**
     * 应用里转链接用这个
     */
    @Bean
    public GmallOssClient publicGmallOssClient(
            @Value("${oss.ak.key}") String accessKey,
            @Value("${oss.ak.security}") String accessSecurity,
            @Value("${oss.endpoint.public}") String endPoint
    ) {
        return new GmallOssClient(endPoint, accessKey, accessSecurity);
    }

    /**
     * 应用里put、get 内容用这个 (走内网链接的oss,应用里读取oss应该走这个client)
     */
    @Bean
    public GmallOssClient internalGmallOssClient(
            @Value("${oss.ak.key}") String accessKey,
            @Value("${oss.ak.security}") String accessSecurity,
            @Value("${oss.endpoint.internal}") String endPoint
    ) {
        return new GmallOssClient(endPoint, accessKey, accessSecurity);
    }
}
