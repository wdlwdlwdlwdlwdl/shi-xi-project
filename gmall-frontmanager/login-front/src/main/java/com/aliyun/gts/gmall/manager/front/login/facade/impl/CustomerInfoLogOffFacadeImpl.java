package com.aliyun.gts.gmall.manager.front.login.facade.impl;

import com.aliyun.gts.gmall.center.user.api.enums.ThirdCustomerAccountTypeEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.adaptor.CustomerLogOffAdaptor;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CustomerLogOffCheckResult;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerInfoLogOffFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAccountTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 14:49:00
 */
@Slf4j
@Service
public class CustomerInfoLogOffFacadeImpl implements CustomerInfoLogOffFacade {

    @Resource
    private CustomerLogOffAdaptor customerLogOffAdaptor;

    @Override
    public RestResponse<Boolean> customerLogOff(Long custId, String reason, String channel) {
        return customerLogOffAdaptor.customerLogOff(custId,
                Lists.newArrayList(ThirdCustomerAccountTypeEnum.TMALL_GENIE.getCode(),
                        CustomerAccountTypeEnum.WECHAT.getCode()),
                reason, channel);
    }

    @Override
    public RestResponse<CustomerLogOffCheckResult> customerLogOffCheck(Long custId) {
        return customerLogOffAdaptor.customerLogOffCheck(custId);
    }

}
