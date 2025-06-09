package com.aliyun.gts.gmall.manager.front.customer.controller;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddressOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerAddressQuery;
import com.aliyun.gts.gmall.manager.front.customer.facade.AddressFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员收货地址操作
 *
 * @author tiansong
 */
@Api(value = "会员收货地址操作", tags = {"customer", "address", "token"})
@RestController
public class AddressController {
    @Autowired
    private AddressFacade addressFacade;

    @ApiOperation(value = "获取会员收货地址列表")
    @PostMapping(name = "queryList", value = "/api/customer/address/queryList/token")
    public @ResponseBody
    RestResponse<List<CustomerAddressDTO>> queryList(@RequestBody CustomerAddressQuery customerAddressQuery) {
        return RestResponse.okWithoutMsg(addressFacade.queryList(customerAddressQuery));
    }

    @ApiOperation(value = "新增或编辑收货地址")
    @PostMapping(name = "addOrEdit", value = "/api/customer/address/addOrEdit/token")
    public @ResponseBody
    RestResponse<CustomerAddressDTO> addOrEdit(@RequestBody AddressCommand addressCommand) {
        return RestResponse.ok(addressFacade.addOrEdit(addressCommand));
    }

    @ApiOperation(value = "设置默认收货地址")
    @PostMapping(name = "setDefaultById", value = "/api/customer/address/setDefaultById/token")
    public @ResponseBody
    RestResponse<Boolean> setDefaultById(@RequestBody AddressOptCommand addressOptCommand) {
        return RestResponse.ok(addressFacade.setDefaultById(addressOptCommand));
    }

    @ApiOperation(value = "删除收货地址")
    @PostMapping(name = "delById", value = "/api/customer/address/delById/token")
    public RestResponse<Boolean> delById(@RequestBody AddressOptCommand addressOptCommand) {
        return RestResponse.ok(addressFacade.delById(addressOptCommand));
    }



    @ApiOperation(value = "查看收货地址详情")
    @PostMapping(name = "detail", value = "/api/customer/address/detail/token")
    public RestResponse<CustomerAddressDTO> detail(@RequestBody AddressOptCommand addressOptCommand) {
        return RestResponse.ok(addressFacade.detail(addressOptCommand));
    }


}