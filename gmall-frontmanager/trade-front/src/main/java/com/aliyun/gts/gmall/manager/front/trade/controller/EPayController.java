package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayAccess;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayCard;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayClients;
import com.aliyun.gts.gmall.manager.front.trade.facade.EPayFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "订单操作", tags = {"trade", "epay", "token"})
@RestController
public class EPayController {

    @Autowired
    private EPayFacade EPayFacade;

    @ApiOperation("获取epay的请求token")
    @PostMapping(name = "getEpayAccessToken", value = "/api/trade/epay/getEpayAccessToken/token")
    public @ResponseBody
    RestResponse<EPayAccess> getEpayAccessToken(@RequestBody EPayQueryReq EPayQueryReq) {
        return RestResponse.okWithoutMsg(EPayFacade.getEpayAccessToken(EPayQueryReq));
    }

    @ApiOperation("获取epay的所有的cards")
    @PostMapping(name = "getEpayCards", value = "/api/trade/epay/getEpayCards/token")
    public @ResponseBody
    RestResponse<List<EPayCard>> getEpayCards(@RequestBody EPayQueryReq EPayQueryReq) {
        ParamUtil.notBlank(EPayQueryReq.getScope(), I18NMessageUtils.getMessage("scope.required"));
        return RestResponse.okWithoutMsg(EPayFacade.getEpayCards(EPayQueryReq));
    }

    @ApiOperation("获取epay支付的token客户端信息")
    @PostMapping(name = "getEpayClients", value = "/api/trade/epay/getEpayClients/token")
    public @ResponseBody
    RestResponse<EPayClients> getEpayClients(@RequestBody EPayQueryReq EPayQueryReq) {
        return RestResponse.okWithoutMsg(EPayFacade.getEpayClients(EPayQueryReq));
    }

    @ApiOperation("获取epay支付支付单的支付状态 - 测试使用")
    @PostMapping(name = "getInvoiceIdStatu", value = "/api/trade/epay/getInvoiceIdStatu/token")
    public @ResponseBody
    RestResponse<String> getInvoiceIdStatu(@RequestBody EPayQueryReq EPayQueryReq) {
        ParamUtil.nonNull(EPayQueryReq.getInvoiceID(), I18NMessageUtils.getMessage("invoiceID.required"));
        return RestResponse.okWithoutMsg(EPayFacade.getInvoiceIdStatu(EPayQueryReq));
    }

    @ApiOperation("退款接口 - 测试使用")
    @PostMapping(name = "refund", value = "/api/trade/epay/refund/token")
    public @ResponseBody
    RestResponse<Boolean> refund(@RequestBody EPayQueryReq EPayQueryReq) {
        return RestResponse.okWithoutMsg(EPayFacade.refund(EPayQueryReq));
    }

}
