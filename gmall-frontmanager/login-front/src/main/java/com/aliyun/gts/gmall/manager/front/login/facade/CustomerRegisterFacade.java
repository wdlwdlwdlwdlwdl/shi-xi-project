package com.aliyun.gts.gmall.manager.front.login.facade;

import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.RegisterResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import io.swagger.annotations.ApiOperation;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月21日 15:39:00
 */
public interface CustomerRegisterFacade {

    @ApiOperation("普通客户注册验证码")
    SecurityCodeResult sendCustomerSecurityCode(SecurityCodeQuery req);


    @ApiOperation("普通客户注册")
    RegisterResult customerRegister(RegisterRestCommand req);

}
