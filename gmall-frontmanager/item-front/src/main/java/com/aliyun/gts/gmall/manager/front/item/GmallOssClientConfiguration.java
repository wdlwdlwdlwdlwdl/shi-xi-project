package com.aliyun.gts.gmall.manager.front.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;

/**
 * oss client
 *
 * @author linyi
 */
@Configuration
public class GmallOssClientConfiguration {
    @Bean
    GmallOssClient publicGmallOssClient(@Value("${oss.ak.key}") String accessKey, @Value("${oss.ak.security}") String accessSecurity,
            @Value("${oss.endpoint.public}") String endPoint) {
        return new GmallOssClient(endPoint, accessKey, accessSecurity);
    }

    /**
     * 走内网链接的oss,应用里读取oss应该走这个client
     *
     * @param accessKey
     * @param accessSecurity
     * @param endPoint
     * @return
     */
    @Bean
    GmallOssClient internalGmallOssClient(@Value("${oss.ak.key}") String accessKey, @Value("${oss.ak.security}") String accessSecurity,
            @Value("${oss.endpoint.internal}") String endPoint) {
        return new GmallOssClient(endPoint, accessKey, accessSecurity);
    }
}
