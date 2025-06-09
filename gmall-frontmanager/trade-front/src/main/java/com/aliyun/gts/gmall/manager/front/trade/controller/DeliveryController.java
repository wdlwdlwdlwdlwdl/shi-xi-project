package com.aliyun.gts.gmall.manager.front.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAccessQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAddressQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryTimeQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryAddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryTimeVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.TradeDeliveryFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * 物流操作
 *
 * @author yangl
 */
@Api(value = "物流地址", tags = {"trade", "order", "token"})
@RestController
public class DeliveryController {

    @Autowired
    private TradeDeliveryFacade deliveryFacade;

    @ApiOperation("物流地址列表")
    @PostMapping(name = "queryDelivery", value = "/api/trade/queryDelivery/token")
    public @ResponseBody
    RestResponse<DeliveryAddressVO> queryDelivery(@RequestBody DeliveryAddressQuery req) {
        return RestResponse.okWithoutMsg(deliveryFacade.queryDeliveryAddress(req));
    }

    @ApiOperation("获取物流时效")
    @PostMapping(name = "queryDeliveryTime", value = "/api/trade/queryDeliveryTime/token")
    public @ResponseBody
    RestResponse<DeliveryTimeVO> queryDeliveryTime(@RequestBody DeliveryTimeQuery req) {
        return RestResponse.okWithoutMsg(deliveryFacade.queryDeliveryTime(req));
    }

    //TODO zhaoqi
//    @ApiOperation("获取支持物流方式")
//    @PostMapping(name = "queryDeliveryType", value = "/api/trade/queryDeliveryType/token")
//    public @ResponseBody
//    RestResponse<DeliveryTypeDetailDTO> queryDeliveryType(@RequestBody DeliveryTypeQueryReq req) {
//        return RestResponse.okWithoutMsg(deliveryFacade.queryDeliveryType(req));
//    }

    @ApiOperation("收货地址物流是否可达")
    @PostMapping(name = "queryDeliveryAccess", value = "/api/trade/queryDeliveryAccess/token")
    public @ResponseBody
    RestResponse queryDeliveryAccess(@RequestBody DeliveryAccessQuery req) {
        return RestResponse.okWithoutMsg(deliveryFacade.queryDeliveryAccess(req));
    }


}