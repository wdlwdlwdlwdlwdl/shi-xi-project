package com.aliyun.gts.gmall.manager.front.service;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:08
 */
public interface ResponseWriteService {
    /**
     * 返回结果
     * @param rest
     * @param response
     */
    void writeJson(RestResponse rest, HttpServletResponse response);

    /**
     *
     * @param request
     * @param key
     * @param value
     */
    void addRequestHeader(HttpServletRequest request, String key, String value);
}
