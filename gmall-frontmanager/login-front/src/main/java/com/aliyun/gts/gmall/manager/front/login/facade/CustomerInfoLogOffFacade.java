package com.aliyun.gts.gmall.manager.front.login.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CustomerLogOffCheckResult;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 14:49:00
 */
public interface CustomerInfoLogOffFacade {

    RestResponse<Boolean> customerLogOff(Long custId, String reason, String channel);

    RestResponse<CustomerLogOffCheckResult> customerLogOffCheck(Long custId);

}
