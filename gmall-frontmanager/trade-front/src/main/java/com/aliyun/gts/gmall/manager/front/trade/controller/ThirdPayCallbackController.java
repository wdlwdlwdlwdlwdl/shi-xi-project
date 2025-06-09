package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.EmptyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.OnlinePayChannelEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayCallBackRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 第三方支付的回调
 * （主要用于支付宝直付通产品二级商家进件创建的回调地址）
 * @author haojing
 */
@Slf4j
@RestController
public class ThirdPayCallbackController {

    /*
    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;

    private static final String OUT_TRADE_NO = "out_trade_no";
    private static final String REFUND_FEE = "refund_fee";

    @ApiOperation("支付宝支付和退款回调")
    @PostMapping(name = "notifyAli", value = "/api/trade/third/pay/ali/notify/")
    public String toPayCallbackForAli(EmptyCommand notUsed, HttpServletRequest request) {
        Map<String, Object> params = convertRequestParamsToMap(request);
        OrderPayCallBackRpcReq orderPayCallBackRpcReq = new OrderPayCallBackRpcReq();
        orderPayCallBackRpcReq.setCallBackParams(params);
        orderPayCallBackRpcReq.setPayChannel(OnlinePayChannelEnum.ALIPAY.getCode());
        orderPayCallBackRpcReq.setOrderChannel("");
        if (Objects.isNull(params.get(OUT_TRADE_NO))) {
            return "fail";
        }
        if (null != params.get(REFUND_FEE) && Strings.isNotEmpty(String.valueOf(params.get(REFUND_FEE)))) {
            //noinspection rawtypes
            RpcResponse rpcResponse = orderPayWriteFacade.refundCallback(orderPayCallBackRpcReq);
            return rpcResponse.isSuccess() ? "success" : "fail";
        } else {
            //noinspection rawtypes
            RpcResponse rpcResponse = orderPayWriteFacade.toPayCallback(orderPayCallBackRpcReq);
            return rpcResponse.isSuccess() ? "success" : "fail";
        }
    }

    private Map<String, Object> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, Object> retMap = new HashMap<>();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;
            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.substring(1));
            } else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }

     */
}
