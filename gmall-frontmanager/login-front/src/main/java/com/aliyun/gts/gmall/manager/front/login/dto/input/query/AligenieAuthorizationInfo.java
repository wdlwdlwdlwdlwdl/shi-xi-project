package com.aliyun.gts.gmall.manager.front.login.dto.input.query;

import lombok.Data;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年10月08日 14:51:00
 */
@Data
public class AligenieAuthorizationInfo {

    /**
     * 用户第三方openId
     */
    private String openId;

    /**
     * 用户手机号码
     */
    private String phoneNumber;

    /**
     * 用户头像地址
     */
    private String avatarUrl;

    /**
     * 用户名称
     */
    private String nickname;

}
