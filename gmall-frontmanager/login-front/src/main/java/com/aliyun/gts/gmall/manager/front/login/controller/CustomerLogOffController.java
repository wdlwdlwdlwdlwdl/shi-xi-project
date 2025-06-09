package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.CustomerLogOffReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.CustomerLogOffCheckResult;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerInfoLogOffFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户注销:
 *  PC端、H5注销： 删除用户数据 和 删除解绑关系数据
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 13:22:00
 */
@Api(value = "用户注销账号", tags = {"customerLogOff"})
@RestController
@RequestMapping(value = "/api/logOff/")
public class CustomerLogOffController {

    @Autowired
    private CustomerInfoLogOffFacade customerInfoLogOffFacade;

    @ApiOperation(value = "PC端 和 H5端，注销账号")
    @PostMapping(name = "customerLogOff", value = "customerLogOff/token")
    RestResponse<Boolean> customerLogOff(@RequestBody CustomerLogOffReq customerLogOffReq) {
        return customerInfoLogOffFacade.customerLogOff(customerLogOffReq.getCustId(),
                customerLogOffReq.getReason(), customerLogOffReq.getChannel());
    }

    @ApiOperation(value = "PC端 和 H5端，注销账号前校验")
    @PostMapping(name = "customerLogOffCheck", value = "customerLogOffCheck/token")
    RestResponse<CustomerLogOffCheckResult> customerLogOffCheck(@RequestBody LoginRestQuery loginRestQuery) {
        return customerInfoLogOffFacade.customerLogOffCheck(loginRestQuery.getCustId());
    }

}
