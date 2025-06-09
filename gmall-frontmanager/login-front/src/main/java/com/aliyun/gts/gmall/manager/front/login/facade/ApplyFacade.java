package com.aliyun.gts.gmall.manager.front.login.facade;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ApplyRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.ApplyQueryResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.RegisterResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import io.swagger.annotations.ApiOperation;

public interface ApplyFacade {


    @ApiOperation("大客户注册验证码")
    SecurityCodeResult sendRegSecurityCode(SecurityCodeQuery req);

    @ApiOperation("大客户入驻验证码")
    SecurityCodeResult sendApplySecurityCode(SecurityCodeQuery req);

    @ApiOperation("大客户注册")
    RegisterResult b2bCustomerRegister(RegisterRestCommand req);

    @ApiOperation("申请入驻")
    void b2bCustomerApply(ApplyRestCommand req);

    @ApiOperation("查询入驻信息")
    ApplyQueryResult queryApply(LoginRestCommand req);

    @ApiOperation("撤回入驻申请")
    void cancelApply(LoginRestCommand req);
}
