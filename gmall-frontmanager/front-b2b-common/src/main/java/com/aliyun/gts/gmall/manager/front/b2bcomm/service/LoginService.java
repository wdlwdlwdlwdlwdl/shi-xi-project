package com.aliyun.gts.gmall.manager.front.b2bcomm.service;

import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;

/**
 * 操作员登录
 *
 * @author 俊贤
 * @date 2021/02/18
 */
public interface LoginService {

    /**
     * 账号密码登录
     *
     * @param
     * @param username
     * @param pwd
     * @return
     */
    OperatorDO loginByPwd(String username, String pwd);
}