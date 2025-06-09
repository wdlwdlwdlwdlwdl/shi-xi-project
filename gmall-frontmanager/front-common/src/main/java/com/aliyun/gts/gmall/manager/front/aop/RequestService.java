package com.aliyun.gts.gmall.manager.front.aop;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
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
@Service
@Slf4j
public class RequestService {

    /**
     * 返回json数据
     * @param rest
     * @param response
     */
    public void writeJson(RestResponse rest,HttpServletResponse response){
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            try {
                response.getWriter()
                        .write(
                                JSONObject.toJSONString(rest)
                        );
            } catch (IOException e) {
                e.printStackTrace();
                log.error("tokenInvalid,",e);
            }
    }
}
