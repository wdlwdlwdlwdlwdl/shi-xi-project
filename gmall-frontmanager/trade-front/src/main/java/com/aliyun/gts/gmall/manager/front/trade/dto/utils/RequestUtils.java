package com.aliyun.gts.gmall.manager.front.trade.dto.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Slf4j
public class RequestUtils {

    public static void consoleLog(HttpServletRequest request, String type) {
        String headers = JSONObject.toJSONString(getHeaders(request));
        String params = "";
        if (null != request.getParameterMap() && !request.getParameterMap().isEmpty()) {
            params = JSONObject.toJSONString(request.getParameterMap());
        }
        String body = getRequestBody(request);
        String msg = "headers = " + headers + ", params = " + params + ", body = " + body;
        log.info(type + msg);
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap();
        List<String> headers = getCommonHeaders();
        headers.add("Postman-Token");
        headers.add("Proxy-Connection");
        headers.add("X-Lantern-Version");
        headers.add("Cookie");
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            if (!headers.contains(headerName)) {
                headerMap.put(headerName, request.getHeader(headerName));
            }
        }

        return headerMap;
    }

    private static List<String> getCommonHeaders() {
        List<String> headers = new ArrayList();
        Class<HttpHeaders> clazz = HttpHeaders.class;
        Field[] fields = clazz.getFields();
        Field[] var3 = fields;
        int var4 = fields.length;
        for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            if (field.getType().toString().endsWith("java.lang.String") && Modifier.isStatic(field.getModifiers())) {
                try {
                    headers.add((String)field.get(HttpHeaders.class));
                } catch (IllegalAccessException var8) {
                    IllegalAccessException e = var8;
                    log.error("反射获取属性值异常-->", e);
                }
            }
        }
        return headers;
    }

    private static String getRequestBody(HttpServletRequest request) {
        StringBuilder requestBody = new StringBuilder();
        try {
            InputStream inputStream = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            reader.close();
            // 返回请求体数据
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return requestBody.toString();
    }
}
