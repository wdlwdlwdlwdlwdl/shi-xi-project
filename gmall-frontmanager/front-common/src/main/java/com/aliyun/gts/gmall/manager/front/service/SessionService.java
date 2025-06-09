package com.aliyun.gts.gmall.manager.front.service;

import com.aliyun.gts.gmall.manager.biz.output.CustDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/11/10 17:28
 */
public interface SessionService {
    /**
     * 根据tocken查询用户
     *
     * @param token
     * @return
     */
    CustDTO getUser(HttpServletRequest request);
}
