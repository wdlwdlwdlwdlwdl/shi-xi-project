package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.UnbindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.BindCustomerResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.PhoneLoginResult;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerBindFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户绑定账号、解绑账号
 * 使用场景： 天猫精灵APP中需要： 绑定账号
 *           天猫精灵APP账号注销时，需要调用   解绑账号接口
 *           我们自己网站PC端账号注销时，需要解绑账号（注销接口内部调用解绑方法，不会调用这里controller层接口）
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 14:37:00
 */
@Api(value = "用户绑定/解绑账号", tags = {"customerBind"})
@RequestMapping(value = "/api/bind/")
@RestController
public class CustomerBindController {

    @Autowired
    private CustomerBindFacade customerBindFacade;

    /**
     *  天猫精灵APP授权登录
     * @Author: alice
     * @param bindCustomerReq:
     * @return
     */
    @ApiOperation(value = "授权绑定账号")
    @PostMapping(name = "bindCustomerRelation", value = "bindCustomerRelation")
    RestResponse<PhoneLoginResult> bindCustomerRelation(@RequestBody BindCustomerReq bindCustomerReq) {
        return RestResponse.okWithoutMsg(customerBindFacade.bindCustomerRelation(bindCustomerReq));
    }

    /**
     * 第三方应用解绑相关账号
     * @Author: alice
     * @param unbindCustomerReq:
     * @return
     */
    @ApiOperation(value = "解绑账号")
    @PostMapping(name = "unbindCustomerRelation", value = "unbindCustomerRelation")
    RestResponse<Boolean> unbindCustomerRelation(@RequestBody UnbindCustomerReq unbindCustomerReq) {
        return customerBindFacade.unbindCustomerRelation(unbindCustomerReq);
    }

    /**
     * 查询绑定的第三方信息
     * @Author: alice
     * @param bindCustomerQuery:
     * @return
     */
    @ApiOperation(value = "查询绑定账号")
    @PostMapping(name = "queryCustomerLoginAccount", value = "queryCustomerLoginAccount/token")
    RestResponse<List<BindCustomerResult>> queryCustomerLoginAccount(@RequestBody BindCustomerQuery bindCustomerQuery) {
        return RestResponse.okWithoutMsg(customerBindFacade.queryCustomerLoginAccount(bindCustomerQuery));
    }
}
