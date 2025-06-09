package com.aliyun.gts.gmall.manager.front.b2bcomm.configuration;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class FrontProperties {

    @Value("${front.content:}")
    private String content;

    public Map<String, ?> toMap() {
        return StringUtils.isBlank(content) ? new HashMap<>() : JSON.parseObject(content);
    }
}
