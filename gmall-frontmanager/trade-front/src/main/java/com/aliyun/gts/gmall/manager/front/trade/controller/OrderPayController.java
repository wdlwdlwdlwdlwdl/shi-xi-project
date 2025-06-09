package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.server.exception.FrontendCare;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.component.PayComponent;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddCardRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.BuyerConfirmPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayResultVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayRenderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PaySearchVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.B2BPayChannelEnum;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderPayFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 订单支付操作
 *
 * @author tiansong
 */
@Api(value = "订单支付操作", tags = {"trade", "pay", "token"})
@RestController
@Slf4j
public class OrderPayController {

    @Autowired
    private OrderPayFacade   orderPayFacade;

    @Autowired
    private GmallFrontConfig gmallFrontConfig;

    @Autowired
    private PayComponent payComponent;

    @ApiOperation("收银台渲染")
    @PostMapping(name = "payRender", value = "/api/trade/pay/payRender/token")
    public @ResponseBody
    RestResponse<PayRenderVO> payRender(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderPayFacade.payRender(primaryOrderRestQuery));
    }

    @ApiOperation("应付单搜索")
    @RequestMapping(value = "/api/trade/pay/search/token")
    public RestResponse<PageInfo<PaySearchVO>> query(@RequestBody PayQueryReq req){
        return RestResponse.okWithoutMsg(payComponent.query(req));
    }

    @ApiOperation("买家确认付款")
    @RequestMapping(value = "/api/trade/pay/confirmPay/token")
    public RestResponse confirmPay(@RequestBody BuyerConfirmPayRestCommand req){
        payComponent.buyerConfirmPay(req);
        return RestResponse.okWithoutMsg(null);
    }

    @ApiOperation("post发起支付")
    @RequestMapping(name = "toPayJson", value = "/api/trade/pay/toPayJson/token")
    public @ResponseBody
    RestResponse<OrderPayResultVO> toPayJson(@RequestBody OrderPayRestCommand orderPayRestCommand, HttpServletRequest request) {
        OrderPayVO orderPayVO = null;
        if (orderPayRestCommand.getExtraPayInfo() == null) {
            orderPayRestCommand.setExtraPayInfo(new HashMap<>());
        }
        orderPayRestCommand.getExtraPayInfo().put("clientIp", request.getRemoteAddr());

        try {
            orderPayVO = orderPayFacade.toPay(orderPayRestCommand);
        } catch (Exception e) {
            String message = null;
            if (e instanceof GmallException) {
                FrontendCare frontendCare = ((GmallException) e).getFrontendCare();
                message = frontendCare.detailMessage();
            }
            log.error("toPay error:", e);
            return RestResponse.fail("", message);
        }

        // 只检查最后一笔订单的回调结果
        String paySuccessUrl = gmallFrontConfig.getPaySuccessUrl(orderPayRestCommand.getOrderChannel());
        paySuccessUrl += ("orderId=" + orderPayRestCommand.getPrimaryOrderId());
        paySuccessUrl += ("&trade_no=" + orderPayRestCommand.getPrimaryOrderId());
        paySuccessUrl += ("&out_trade_no=" + orderPayVO.getPayFlowId());
        paySuccessUrl += ("&primaryOrderId=" + orderPayRestCommand.getPrimaryOrderId());
        paySuccessUrl += ("&payFlowId=" + orderPayVO.getPayFlowId());
        paySuccessUrl += ("&timeout=3");

        OrderPayResultVO r = new OrderPayResultVO();
        r.setPaySuccessUrl(paySuccessUrl);
        r.setPrimaryOrderId(String.valueOf(orderPayRestCommand.getPrimaryOrderId()));
        r.setPayFlowId(orderPayVO.getPayFlowId());

        // 纯积分 || mock支付
        if (StringUtils.isBlank(orderPayVO.getPayData())
                || "MOCK".equalsIgnoreCase(orderPayVO.getPayData())) {
            r.setDirectPaySuccess(true);
            return RestResponse.okWithoutMsg(r);
        }
        // 正常在线支付
        else {
            r.setPayData(orderPayVO.getPayData());
            try {
                JSONObject o = JSON.parseObject(orderPayVO.getPayData());
                r.setPayData(o);
            } catch (Exception e) {
                //ignore
            }
        }
        return RestResponse.okWithoutMsg(r);
    }

    /**
     * 支付宝表单
     *
     * @param payData
     * @return
     */
    private String getBody(String payData) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\" />\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"chrome,IE=edge\" />\n" +
                "  <meta name=\"HandheldFriendly\" content=\"true\" />\n" +
                "  <meta name=\"format-detection\" content=\"telephone=no,email=no\" />\n" +
                "  <meta name=\"viewport\"\n" +
                "    content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0, user-scalable=no, minimal-ui, viewport-fit=cover\" />\n" +
                "  <meta name=\"renderer\" content=\"webkit\" />\n" +
                "  <meta name=\"msapplication-tap-highlight\" content=\"no\" />\n" +
                "  <title>正在支付中</title>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"margin-top:60px;text-align: center;\">\n" +
                "  正在支付中\n" +
                "  <!-- 支付宝代码 -->\n"
                + payData +
                "</body>\n" +
                "</html>";
    }

    @ApiOperation("支付成功检查")
    @PostMapping(name = "payCheck", value = "/api/trade/pay/payCheck/token")
    public @ResponseBody
    RestResponse<Boolean> payCheck(@RequestBody PayCheckRestQuery payCheckRestQuery) {
        return RestResponse.okWithoutMsg(orderPayFacade.payCheck(payCheckRestQuery));
    }

    @ApiOperation("获取保存卡参数")
    @RequestMapping(name = "addPay", value = "/api/trade/epay/addPay/token")
    public @ResponseBody
    RestResponse<JSONObject> addPay(HttpServletRequest request, HttpServletResponse response) {
        CustDTO loginUser = UserHolder.getUser();
        //加卡配置读取
        JSONObject jsonObject = orderPayFacade.addPay();
        jsonObject.put("accountId", loginUser.getAccountId());
        return RestResponse.ok(jsonObject);
    }

    @ApiOperation("获取用户所有的可支付的cardIds")
    @RequestMapping(name = "getCardIds", value = "/api/trade/epay/getCardIds/token")
    public @ResponseBody
    RestResponse<List<JSONObject>> getCardIds(@RequestBody EPayQueryReq ePayQueryReq) {
        ePayQueryReq.setScope("webapi");
        CustDTO loginUser = UserHolder.getUser();
        ePayQueryReq.setAccountId(loginUser.getAccountId());
        List<JSONObject> list = orderPayFacade.getCardIds(ePayQueryReq);
        return RestResponse.okWithoutMsg(list);
    }

    @ApiOperation("获取用户积分")
    @RequestMapping(name = "getBonuses", value = "/api/trade/epay/getBonuses/token")
    public @ResponseBody
    RestResponse<Long> getBonuses(@RequestBody EPayQueryReq ePayQueryReq) {
        Long bonuses = orderPayFacade.getBonuses(ePayQueryReq);
        return RestResponse.ok(bonuses);
    }

    @ApiOperation("移除卡")
    @RequestMapping(name = "removeCard", value = "/api/trade/epay/removeCard/token")
    public @ResponseBody
    RestResponse<Boolean> removeCard(@RequestBody EPayQueryReq ePayQueryReq) {
        Boolean b = orderPayFacade.removeCard(ePayQueryReq);
        return RestResponse.ok(b);
    }

    @ApiOperation("获取用户所有的可支付的card")
    @RequestMapping(name = "getCardIds", value = "/api/trade/epay/getCards/token")
    public @ResponseBody
    RestResponse<List<JSONObject>> getCards(@RequestBody EPayQueryReq ePayQueryReq) {
        CustDTO loginUser = UserHolder.getUser();
        List<JSONObject> list = orderPayFacade.getBankCards(loginUser.getAccountId());
        return RestResponse.ok(list);
    }

    @ApiOperation("后端发起 epay 支付 ")
    @RequestMapping(name = "toPay", value = "/api/trade/epay/toPay/token")
    public @ResponseBody
    RestResponse<Boolean> toEPay(@RequestBody OrderPayRestCommand orderPayRestCommand, HttpServletRequest request)  {
        if (orderPayRestCommand.getExtraPayInfo() == null) {
            orderPayRestCommand.setExtraPayInfo(new HashMap<>());
        }
        orderPayRestCommand.getExtraPayInfo().put("clientIp", request.getRemoteAddr());
        CustDTO loginUser = UserHolder.getUser();
        orderPayRestCommand.setAccountId(loginUser.getAccountId());
        orderPayRestCommand.setCustId(loginUser.getCustId());
        // 创建支付数据
        OrderPayVO orderPayVO = orderPayFacade.toPay(orderPayRestCommand);
        if (null == orderPayVO && StringUtils.isEmpty(orderPayVO.getCartId())) {
            throw new FrontManagerException(TradeFrontResponseCode.PAY_ADD_ERROR, orderPayVO);
        }
        if (PayChannelEnum.EPAY.getCode().equals(orderPayRestCommand.getPayChannel())) {
            ParamUtil.nonNull(orderPayRestCommand.getAccountId(), I18NMessageUtils.getMessage("accountId required"));
            //ParamUtil.nonNull(orderPayRestCommand.getId(), I18NMessageUtils.getMessage("id required"));
            // 开始支付
            String result = orderPayFacade.payment(orderPayRestCommand, orderPayVO);
            log.info("==================paymentResult=======================");
            log.info(result);
            log.info("=========================================");
            boolean success = getPayResult(result);
            return RestResponse.ok(success);
        }
        return RestResponse.ok(false);
    }

    private boolean getPayResult(String result) {
        boolean success = false;
        try {
            if (StringUtils.isNotEmpty(result) && "0".equals(JSONObject.parseObject(result).getString("code"))) {
                success = true;
            }
        }catch (Exception e){
            log.error("Epay payment error: "+e.getMessage());
        }
        return success;
    }

    @ApiOperation("添加epay 卡")
    @RequestMapping(name = "addCard", value = "/api/trade/epay/addCard/token")
    public @ResponseBody
    RestResponse<String> addCard(@RequestBody AddCardRestCommand addCardRestCommand) {
        if("ok".equals(addCardRestCommand.getCode()) && "success".equals(addCardRestCommand.getReason())){
            orderPayFacade.addCard(addCardRestCommand);
        }
        return RestResponse.ok("success");
    }

    /**
     * 发起支付 版本V2
     * @param orderPayRestCommand
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    @ApiOperation("后端发起 epay 支付 ")
    @RequestMapping(name = "toPayV2", value = "/api/trade/epay/toPay/v2/token")
    public @ResponseBody
    RestResponse<Boolean> toEPayV2(@RequestBody OrderPayRestCommand orderPayRestCommand, HttpServletRequest request) {
        if (orderPayRestCommand.getExtraPayInfo() == null) {
            orderPayRestCommand.setExtraPayInfo(new HashMap<>());
        }
        orderPayRestCommand.getExtraPayInfo().put("clientIp", request.getRemoteAddr());
        CustDTO loginUser = UserHolder.getUser();
        orderPayRestCommand.setAccountId(loginUser.getAccountId());
        orderPayRestCommand.setCustId(loginUser.getCustId());
        return orderPayFacade.payOrder(orderPayRestCommand);
    }
}