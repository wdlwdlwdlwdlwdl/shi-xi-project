package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月26日 17:24:00
 */
@Data
public class BindCustomerResult extends AbstractOutputInfo {

    private String nickName;

    private String openId;

    private String type;

    private String typeDesc;

}
