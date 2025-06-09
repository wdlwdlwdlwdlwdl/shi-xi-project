package com.aliyun.gts.gmall.manager.front.b2bcomm.service;

import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/3 20:12
 */
public interface SessionService {
    /**
     * 通过session找到登陆对象
     * @param token
     * @return
     */
    public OperatorDO getUserByToken(String token);

    /**
     * 把数据加到cookie里面去
     * cookie
     * @return
     */
    public boolean addToRedis(String token, OperatorDO operatorDO);

    /**
     * 生成token
     * @return
     */
    public String generateToken(OperatorDO operatorDO);

    /**
     * 失效token
     * @return
     */
    public void expireToken(String token);

    /**
     * 通过http上下文找到token
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request);
}
