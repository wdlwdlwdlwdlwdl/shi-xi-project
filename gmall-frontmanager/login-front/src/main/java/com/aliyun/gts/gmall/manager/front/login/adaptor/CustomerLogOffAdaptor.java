package com.aliyun.gts.gmall.manager.front.login.adaptor;

import com.aliyun.gts.gmall.center.user.api.dto.input.CustomerLogOffCheckReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustomerLogOffReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerLogOffFacade;
import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CustomerLogOffCheckResult;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月23日 10:58:00
 */
@Slf4j
@Service
public class CustomerLogOffAdaptor {

    @Resource
    private CustomerLogOffFacade customerLogOffFacade;

    @Resource
    private LoginFacade loginFacade;

    public RestResponse<Boolean> customerLogOff(Long custId, List<String> typeList, String reason, String channel) {
        RestResponse restResponse = new RestResponse();
        CustomerLogOffReq req = new CustomerLogOffReq();
        req.setCustId(custId);
        req.setTypeList(typeList);
        req.setReason(reason);
        RpcResponse<Boolean> response = customerLogOffFacade.customerLogOff(req);
        restResponse.setSuccess(response.isSuccess());
        restResponse.setData(response.getData());
        restResponse.setCode(CommonResponseCode.Success.getCode());
        FailInfo fail = response.getFail();
        if (Objects.nonNull(fail)) {
            restResponse.setCode(fail.getCode());
            restResponse.setMessage(fail.getMessage());
            return restResponse;
        }
        loginFacade.refreshToken(custId, ChannelEnum.get(channel), null);
        return restResponse;
    }

    public RestResponse<CustomerLogOffCheckResult> customerLogOffCheck(Long custId) {
        CustomerLogOffCheckReq req = new CustomerLogOffCheckReq();
        req.setCustId(custId);
        RpcResponse<Boolean> response = customerLogOffFacade.customerLogOffCheck(req);
        CustomerLogOffCheckResult customerLogOffCheckResult = new CustomerLogOffCheckResult();
        customerLogOffCheckResult.setCheckSuccess(response.isSuccess());
        customerLogOffCheckResult.setCode(CommonResponseCode.Success.getCode());
        FailInfo fail = response.getFail();
        if (Objects.nonNull(fail)) {
            customerLogOffCheckResult.setCheckSuccess(Boolean.FALSE);
            customerLogOffCheckResult.setCode(fail.getCode());
            customerLogOffCheckResult.setMessage(fail.getMessage());
        }
        return RestResponse.okWithoutMsg(customerLogOffCheckResult);
    }

}
