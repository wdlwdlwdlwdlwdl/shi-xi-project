package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceApplyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceInfoCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceApplyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderInvoiceFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "订单发票操作", tags = {"trade", "invoice", "token"})
@RestController
public class OrderInvoiceController {

    @Autowired
    private OrderInvoiceFacade orderInvoiceFacade;

    @ApiOperation("申请发票")
    @PostMapping(name = "applyOrderInvoice", value = "/api/trade/order/invoice/apply/token")
    public @ResponseBody
    RestResponse<InvoiceApplyVO> applyOrderInvoice(@RequestBody OrderInvoiceApplyCommand orderInvoiceApplyCommand) {
        return RestResponse.ok(orderInvoiceFacade.applyInvoice(orderInvoiceApplyCommand));
    }


    @ApiOperation("查看发票")
    @PostMapping(name = "getOrderInvoiceInfo", value = "/api/trade/order/invoice/get/token")
    public @ResponseBody
    RestResponse<InvoiceDetailVO> getOrderInvoice(@RequestBody OrderInvoiceInfoCommand orderInvoiceInfoCommand) {
        return RestResponse.ok(orderInvoiceFacade.getInvoiceInfo(orderInvoiceInfoCommand));
    }
}
