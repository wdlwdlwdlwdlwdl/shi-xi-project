package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddBankCardCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.BindedCardListQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.B2BPayChannelEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.ApiPayFacade;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderPayFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.epay.EpayCardInfoDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * api接口支付Controller
 * @author liangweiwei
 */
@Api(value = "api接口支付",tags = "api接口支付")
@RestController
@Slf4j
public class ApiPayController {
    @Autowired
    private ApiPayFacade apiPayFacade;

    @Autowired
    private OrderPayFacade orderPayFacade;

    @ApiOperation("后端接口支付")
    @PostMapping(name = "apiPay", value = "/api/trade/apiPay/pay")
    @ResponseBody
    public RestResponse<OrderPayVO> apiPay(@RequestBody OrderPayRestCommand orderPayRestCommand, HttpServletRequest request) throws UnsupportedEncodingException {
        if (B2BPayChannelEnum.ACCOUNT_PERIOD.getCode().equals(orderPayRestCommand.getPayChannel())) {
            String accountPeriod = new String(request.getParameter("accountPeriod").getBytes("ISO8859-1"), "UTF-8");
            orderPayRestCommand.setAccountPeriod(accountPeriod);
        }
        if (orderPayRestCommand.getExtraPayInfo() == null) {
            orderPayRestCommand.setExtraPayInfo(new HashMap<>());
        }
        orderPayRestCommand.getExtraPayInfo().put("clientIp", request.getRemoteAddr());
        // 创建支付数据
        OrderPayVO orderPayVO = orderPayFacade.toPay(orderPayRestCommand);
        if (null == orderPayVO && StringUtils.isEmpty(orderPayVO.getCartId())) {
            throw new FrontManagerException(TradeFrontResponseCode.PAY_ADD_ERROR, orderPayVO);
        }
        // 开始支付
        boolean result = apiPayFacade.payment(orderPayRestCommand, orderPayVO);
        if (result) {
            // 将支付的数据同步到支付中心
            PayCheckRestQuery payCheckRestQuery = new PayCheckRestQuery();
            payCheckRestQuery.setCartId(orderPayVO.getCartId());
            payCheckRestQuery.setPrimaryOrderList(orderPayRestCommand.getPrimaryOrderList());
            payCheckRestQuery.setPayFlowId(orderPayVO.getPayFlowId());
            // 更新订单状态
            Boolean checkBoolean = orderPayFacade.payCheck(payCheckRestQuery);
            if (!checkBoolean) {
                log.error("支付中心支付失败，订单号：" + payCheckRestQuery.getPrimaryOrderId());
            }
        } else {
            throw new FrontManagerException(TradeFrontResponseCode.BACKEND_PAYMENT_ERROR, result);
        }
        return RestResponse.ok(orderPayVO);
    }

    @ApiOperation("加卡")
    @PostMapping(name = "addCard", value = "/api/trade/apiPay/addCard")
    @ResponseBody
    public RestResponse<Boolean> addCard(@RequestBody AddBankCardCommand cmd){
       boolean isSuccess =apiPayFacade.addCard(cmd);
        return RestResponse.ok(isSuccess);
    }

    @ApiOperation("查询已绑卡列表")
    @PostMapping(name = "查询已绑卡列表", value = "/api/trade/apiPay/listBindCards")
    @ResponseBody
    public RestResponse<List<EpayCardInfoDto>> listBindCards(@RequestBody BindedCardListQuery cmd){
        List<EpayCardInfoDto> cardList =apiPayFacade.listUserBindCard(cmd);
        return RestResponse.ok(cardList);
    }



}
