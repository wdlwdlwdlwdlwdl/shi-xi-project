package com.aliyun.gts.gmall.manager.front.common.config;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 返回对象json处理
 *
 * @author tiansong
 */
@ControllerAdvice
@Slf4j
public class DataResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DegradationConfig degradationConfig;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        if (degradationConfig.isFilterFiled()) {
            return (AnnotatedElementUtils.hasAnnotation(methodParameter.getContainingClass(), ResponseBody.class) ||
                methodParameter.hasMethodAnnotation(ResponseBody.class));
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object object, MethodParameter methodParameter, MediaType mediaType, Class aClass,
        ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Map<String, Object> responseData = Maps.newHashMap();
        try {
            if (object instanceof RpcResponse || object instanceof RestResponse) {
                String result = objectMapper.writeValueAsString(object);
                responseData = JSON.parseObject(result, Map.class);
                object = responseData;
            }
        } catch (JsonProcessingException e) {
            responseData.put("code", FrontCommonResponseCode.DATA_PARSE_ERROR.getCode());
            responseData.put("message", FrontCommonResponseCode.DATA_PARSE_ERROR.getMessage());
            object = responseData;
            log.error("DataResponseBodyAdvice.beforeBodyWrite parse data error:", e);
        }
        return object;
    }
}
