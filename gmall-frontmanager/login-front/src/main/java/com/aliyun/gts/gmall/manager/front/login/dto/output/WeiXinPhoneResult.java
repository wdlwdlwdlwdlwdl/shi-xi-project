package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年10月09日 17:22:00
 */
@Data
public class WeiXinPhoneResult extends AbstractOutputInfo {

    /**
     * 用户绑定的手机号（国外手机号会有区号）
     */
    private String phoneNumber;

    /**
     * 没有区号的手机号
     */
    private String purePhoneNumber;

    /**
     * 区号
     */
    private String countryCode;



}
