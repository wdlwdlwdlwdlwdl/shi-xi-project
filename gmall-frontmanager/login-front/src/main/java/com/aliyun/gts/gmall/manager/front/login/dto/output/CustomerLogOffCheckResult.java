package com.aliyun.gts.gmall.manager.front.login.dto.output;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Data;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月29日 17:26:00
 */
@Data
public class CustomerLogOffCheckResult extends AbstractOutputInfo {

    private Boolean checkSuccess;

    private String code;

    private String message;

}
