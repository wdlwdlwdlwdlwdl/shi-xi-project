package com.aliyun.gts.gmall.manager.front.customer.controller;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.InvoiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发票操作
 *
 * @author tiansong
 */
@Api(value = "发票操作", tags = {"customer", "invoice", "token"})
@RestController
public class InvoiceController {
    @Autowired
    private InvoiceFacade invoiceFacade;

    @ApiOperation(value = "发票列表")
    @PostMapping(name = "queryList", value = "/api/customer/invoice/queryList/token")
    public @ResponseBody
    RestResponse<List<CustomerInvoiceVO>> queryList(@RequestBody LoginRestQuery loginRestQuery) {
        return RestResponse.okWithoutMsg(invoiceFacade.queryList(loginRestQuery));
    }

    @ApiOperation(value = "新增或编辑发票")
    @PostMapping(name = "addOrEdit", value = "/api/customer/invoice/addOrEdit/token")
    public @ResponseBody
    RestResponse<Long> addOrEdit(@RequestBody InvoiceCommand invoiceCommand) {
        return RestResponse.ok(invoiceFacade.addOrEdit(invoiceCommand));
    }

    @ApiOperation(value = "删除发票")
    @PostMapping(name = "delById", value = "/api/customer/invoice/delById/token")
    public RestResponse<Boolean> delById(@RequestBody InvoiceOptCommand invoiceOptCommand) {
        return RestResponse.ok(invoiceFacade.delById(invoiceOptCommand));
    }
}