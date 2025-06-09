package com.aliyun.gts.gmall.manager.front.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 返回对象格式处理
 *
 * @author tiansong
 */
@Configuration
public class ResponseDataConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        // 过滤对象中的null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 日期Date格式不做处理
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        // KEY大小写敏感，不对KEY做驼峰处理
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return objectMapper;
    }
}
