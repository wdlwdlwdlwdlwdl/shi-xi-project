package com.aliyun.gts.gmall.manager.front.customer.controller;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginGroupRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerInfoCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditCustomerLanguageCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditHeadUrlCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.EditPasswordCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerGrowthQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.*;
import com.aliyun.gts.gmall.manager.front.customer.facade.CustomerFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员信息操作
 *
 * @author tiansong
 */
@Api(value = "会员信息操作", tags = {"customer", "token"})
@RestController
public class CustomerController {
    @Autowired
    private CustomerFacade customerFacade;

    @ApiOperation(value = "修改登录密码")
    @PostMapping(name = "editPassword", value = "/api/customer/editPassword/token")
    public @ResponseBody
    RestResponse<Boolean> editPassword(@RequestBody EditPasswordCommand editPasswordCommand) {
        return RestResponse.ok(customerFacade.editPassword(editPasswordCommand));
    }

    @ApiOperation(value = "编辑用户头像")
    @PostMapping(name = "editHeadUrl", value = "/api/customer/editHeadUrl/token")
    public @ResponseBody
    RestResponse<Boolean> editHeadUrl(@RequestBody EditHeadUrlCommand editHeadUrlCommand) {
        return RestResponse.ok(customerFacade.editHeadUrl(editHeadUrlCommand));
    }

    @ApiOperation(value = "编辑用户基础信息")
    @PostMapping(name = "editBaseInfo", value = "/api/customer/editBaseInfo/token")
    public @ResponseBody
    RestResponse<Boolean> editBaseInfo(@RequestBody EditCustomerInfoCommand editCustomerInfoCommand) {
        return RestResponse.ok(customerFacade.editBaseInfo(editCustomerInfoCommand));
    }

    @ApiOperation(value = "编辑用户语言")
    @PostMapping(name = "editLanguageInfo", value = "/api/customer/editLanguageInfo/token")
    public @ResponseBody
    RestResponse<String> editLanguageInfo(@RequestBody EditCustomerLanguageCommand editCustomerLanguageCommand) {
        return customerFacade.editLanguageInfo(editCustomerLanguageCommand);
    }

    @ApiOperation(value = "查询用户基础信息")
    @PostMapping(name = "queryById", value = "/api/customer/queryById/token")
    public @ResponseBody
    RestResponse<CustomerVO> queryById(@RequestBody LoginRestQuery loginRestQuery) {
        return RestResponse.okWithoutMsg(customerFacade.queryById(loginRestQuery));
    }

    @ApiOperation(value = "查询用户等级列表")
    @PostMapping(name = "queryLevelList", value = "/api/customer/queryLevelList/token")
    public @ResponseBody
    RestResponse<List<CustomerLevelVO>> queryLevelList(@RequestBody LoginRestQuery loginRestQuery) {
        return RestResponse.okWithoutMsg(customerFacade.queryLevelList(loginRestQuery));
    }

    @ApiOperation(value = "判断是否新用户 true:是 false:不是")
    @PostMapping(name = "queryIsNewUser", value = "/api/customer/queryIsNewUser/token")
    public @ResponseBody
    RestResponse<NewCustomerVO> queryIsNewUser(@RequestBody LoginGroupRestQuery loginGroupRestQuery) {
        return RestResponse.okWithoutMsg(customerFacade.isNewCust(loginGroupRestQuery));
    }
    @ApiOperation(value = "测试redis")
    @PostMapping(name = "testRedis", value = "/api/customer/test/redis")
    public RestResponse<Object> testRedis(@RequestBody EmptyRestQuery req) {
        return RestResponse.ok(customerFacade.testRedis());
    }


    @ApiOperation(value = "查询用户成长值记录列表")
    @PostMapping(name = "queryGrowthRecords", value = "/api/customer/queryGrowthRecords")
    public @ResponseBody
    RestResponse<PageInfo<CustomerGrowthRecordVO>> queryGrowthRecords(@RequestBody CustomerGrowthQuery customerGrowthQuery) {
        return RestResponse.okWithoutMsg(customerFacade.queryGrowthRecords(customerGrowthQuery));
    }




    @ApiOperation(value = "查询用户成长值分类统计列表")
    @PostMapping(name = "queryCustomerGrowths", value = "/api/customer/queryCustomerGrowths")
    public @ResponseBody
    RestResponse<List<CustomerGrowthVO>> queryCustomerGrowths(@RequestBody CustomerGrowthQuery customerGrowthQuery) {
        return RestResponse.okWithoutMsg(customerFacade.queryCustomerGrowths(customerGrowthQuery));
    }


}
