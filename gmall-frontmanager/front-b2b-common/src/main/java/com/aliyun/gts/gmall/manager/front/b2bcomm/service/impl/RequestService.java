package com.aliyun.gts.gmall.manager.front.b2bcomm.service.impl;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/5 13:39
 */
//@Slf4j
//public class RequestService {
//    private static final ObjectMapper OBJECT_MAPPER;
//    static {
//        OBJECT_MAPPER = new ObjectMapper();
//        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//    };
//
//    /**
//     * 返回json数据
//     * @param rest
//     * @param response
//     */
//    public void writeJson(RestResponse rest,HttpServletResponse response){
//            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//            try {
//                response.getWriter()
//                        .write(
//                                OBJECT_MAPPER.writeValueAsString(rest)
//                        );
//            } catch (IOException e) {
//                e.printStackTrace();
//                log.error("tokenInvalid,",e);
//            }
//    }
//}
