package com.aliyun.gts.gmall.manager.front.service.impl;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.service.ResponseWriteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:09
 */
@Component
@Slf4j
public class ResponseWriteServiceImpl implements ResponseWriteService {
    private static final ObjectMapper OBJECT_MAPPER;
    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void writeJson(RestResponse rest, HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            response.getWriter().write(
                OBJECT_MAPPER.writeValueAsString(rest)
            );
        } catch (IOException e) {
            e.printStackTrace();
            log.error("tokenInvalid,", e);
        }
    }

    @Override
    public void addRequestHeader(HttpServletRequest request, String key, String value){
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        try {
            Field requestField = requestClass.getDeclaredField("request");
            requestField.setAccessible(true);
            Object requestObj = requestField.get(request);
            Field coyoteRequestField = requestObj.getClass().getDeclaredField("coyoteRequest");
            coyoteRequestField.setAccessible(true);
            Object coyoteRequestObj = coyoteRequestField.get(requestObj);
            Field headersField = coyoteRequestObj.getClass().getDeclaredField("headers");
            headersField.setAccessible(true);
            MimeHeaders headersObj = (MimeHeaders)headersField.get(coyoteRequestObj);
            headersObj.removeHeader(key);
            headersObj.addValue(key).setString(value);
        } catch (Exception e) {
            log.error("reflect set header {} error {}", key, e);
        }
    }
}
