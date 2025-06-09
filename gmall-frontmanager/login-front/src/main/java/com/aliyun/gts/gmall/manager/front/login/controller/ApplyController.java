package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.biz.adapter.DictAdapter;
import com.aliyun.gts.gmall.manager.biz.output.DictVO;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ApplyRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.*;
import com.aliyun.gts.gmall.manager.front.login.facade.ApplyFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "大客户入驻", tags = {"apply"})
public class ApplyController {

    @Autowired
    private ApplyFacade applyFacade;
    @Autowired
    private DictAdapter dictAdapter;

    @ApiOperation(value = "大客户注册验证码")
    @PostMapping(name = "sendRegSecurityCode", value = "/api/login/apply/sendRegSecurityCode")
    public @ResponseBody
    RestResponse<SecurityCodeResult> sendRegSecurityCode(@RequestBody SecurityCodeQuery req) {
        return RestResponse.okWithoutMsg(applyFacade.sendRegSecurityCode(req));
    }

    @ApiOperation(value = "大客户入驻验证码")
    @PostMapping(name = "sendApplySecurityCode", value = "/api/login/apply/sendApplySecurityCode")
    public @ResponseBody
    RestResponse<SecurityCodeResult> sendApplySecurityCode(@RequestBody SecurityCodeQuery req) {
        return RestResponse.okWithoutMsg(applyFacade.sendApplySecurityCode(req));
    }

    @ApiOperation(value = "大客户注册")
    @PostMapping(name = "b2bCustomerRegister", value = "/api/login/apply/b2bCustomerRegister")
    public @ResponseBody
    RestResponse<RegisterResult> b2bCustomerRegister(@RequestBody RegisterRestCommand req) {
        return RestResponse.okWithoutMsg(applyFacade.b2bCustomerRegister(req));
    }

    @ApiOperation(value = "查银行字典")
    @PostMapping(name = "queryBanks", value = "/api/login/apply/queryBanks")
    public @ResponseBody
    RestResponse<DictVO> queryBanks(@RequestBody EmptyRestQuery req) {
        return RestResponse.okWithoutMsg(dictAdapter.queryByKey("bankName"));
    }

    @ApiOperation(value = "申请入驻")
    @PostMapping(name = "b2bCustomerApply", value = "/api/login/apply/b2bCustomerApply")
    public @ResponseBody
    RestResponse<Void> b2bCustomerApply(@RequestBody ApplyRestCommand req) {
        applyFacade.b2bCustomerApply(req);
        return RestResponse.okWithoutMsg(null);
    }

    @ApiOperation(value = "查询入驻信息")
    @PostMapping(name = "queryApply", value = "/api/login/apply/queryApply")
    public @ResponseBody
    RestResponse<ApplyQueryResult> queryApply(@RequestBody LoginRestCommand req) {
        return RestResponse.okWithoutMsg(applyFacade.queryApply(req));
    }

    @ApiOperation(value = "撤回入驻申请")
    @PostMapping(name = "cancelApply", value = "/api/login/apply/cancelApply")
    public @ResponseBody
    RestResponse<Void> cancelApply(@RequestBody LoginRestCommand req) {
        applyFacade.cancelApply(req);
        return RestResponse.okWithoutMsg(null);
    }
}
