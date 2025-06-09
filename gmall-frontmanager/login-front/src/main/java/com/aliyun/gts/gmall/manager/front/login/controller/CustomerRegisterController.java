package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.RegisterResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerRegisterFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月21日 15:34:00
 */
@RestController
@Api(value = "普通客户注册", tags = {"customerRegister"})
public class CustomerRegisterController {

    @Autowired
    private CustomerRegisterFacade customerRegisterFacade;

    @ApiOperation(value = "普通客户注册验证码")
    @PostMapping(name = "sendCustomerSecurityCode", value = "/api/login/apply/sendCustomerSecurityCode")
    public RestResponse<SecurityCodeResult> sendCustomerSecurityCode(@RequestBody SecurityCodeQuery req) {
        SecurityCodeResult securityCodeResult = customerRegisterFacade.sendCustomerSecurityCode(req);
        if (Objects.nonNull(securityCodeResult) && !securityCodeResult.getSuccess()) {
            return RestResponse.fail(LoginFrontResponseCode.SEND_SECURITY_CODE_FAIL.getCode(), securityCodeResult.getMessage());
        }
        return RestResponse.okWithoutMsg(securityCodeResult);
    }

    @ApiOperation(value = "普通客户注册")
    @PostMapping(name = "customerRegister", value = "/api/login/apply/customerRegister")
    public RestResponse<RegisterResult> customerRegister(@RequestBody RegisterRestCommand req) {
        return RestResponse.okWithoutMsg(customerRegisterFacade.customerRegister(req));
    }

}
