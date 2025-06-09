package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CountOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.OrderListRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单操作
 *
 * @author tiansong
 */
@Api(value = "订单操作", tags = {"trade", "order", "token"})
@RestController
public class OrderController {

    @Autowired
    private OrderFacade orderFacade;

    @ApiOperation("订单确认前Check")
    @PostMapping(name = "checkConfirm", value = "/api/trade/order/checkConfirm/token")
    public @ResponseBody
    RestResponse<Boolean> checkConfirm(@RequestBody ConfirmOrderRestQuery confirmOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.checkConfirm(confirmOrderRestQuery));
    }

    @ApiOperation("订单确认")
    @PostMapping(name = "confirm", value = "/api/trade/order/confirm/token")
    public @ResponseBody
    RestResponse<OrderConfirmVO> confirm(@RequestBody ConfirmOrderRestQuery confirmOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.confirm(confirmOrderRestQuery));
    }

    @ApiOperation("创建订单")
    @PostMapping(name = "createOrder", value = "/api/trade/order/createOrder/token")
    public @ResponseBody
    RestResponse<OrderCreateResultVO> createOrder(@RequestBody CreateOrderRestCommand createOrderRestCommand) {
        return RestResponse.ok(orderFacade.createOrder(createOrderRestCommand));
    }

    /**
     * 订单确认 新版本
     * 确认商品信息是否可以下订单 并返回下单依赖的信息
     *    根据商品判断商品信息计算商品收货物流，价格，支付方式等信息
     *    如果确认失败 则不可以调用自动下单接口
     *    如果已经存在了缓存且生成临时单， 不在调用下单接口
     *    确认订单场景 不做拆单 就以商品为维度计算！！ 下单的时候再拆单！！！
     * @anthor shifeng
     * @param confirmOrderRestQuery
     * @return
     */
    @ApiOperation("订单确认")
    @PostMapping(name = "confirmV2", value = "/api/trade/order/confirm/v2/token")
    public @ResponseBody
    RestResponse<OrderConfirmVO> confirmOrderNew(@RequestBody ConfirmOrderRestQuery confirmOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.confirmNew(confirmOrderRestQuery));
    }

    /**
     * 订单确认 新版本
     * 确认商品信息是否可以下订单 并返回下单依赖的信息
     *    根据商品判断商品信息计算商品收货物流，价格，支付方式等信息
     *    如果确认失败 则不可以调用自动下单接口
     *    如果已经存在了缓存且生成临时单， 不在调用下单接口
     *    确认订单场景 不做拆单 就以商品为维度计算！！ 下单的时候再拆单！！！
     * @anthor shifeng
     * @param
     * @return
     */
    @ApiOperation("订单结算 临时生成")
    @PostMapping(name = "checkout", value = "/api/trade/order/checkout/v2/token")
    public @ResponseBody
    RestResponse<CheckOutOrderResultVO> checkOutOrderNew(@RequestBody CreateCheckOutOrderRestCommand createCheckOutOrderRestCommand) {
        return RestResponse.okWithoutMsg(orderFacade.checkOutOrderNew(createCheckOutOrderRestCommand));
    }

    /**
     * 创建订单 新版本
     * 使用confirm的数据 下订单
     *    入参数据 和confirm缓存数据比较 ，必须相同
     *    二次计算拆单 价格 运费  ，获取的数据 必须相同
     *    保存订单 删除生成的临时结算单
     * @param createOrderRestCommand
     * @return
     */
    @ApiOperation("创建订单")
    @PostMapping(name = "createOrderV2", value = "/api/trade/order/createOrder/v2/token")
    public @ResponseBody
    RestResponse<OrderCreateResultVO> createOrderNew(@RequestBody CreateOrderRestCommand createOrderRestCommand) {
        return RestResponse.ok(orderFacade.createOrderNew(createOrderRestCommand));
    }

    @ApiOperation("订单详情")
    @PostMapping(name = "getDetail", value = "/api/trade/order/getDetail/token")
    public @ResponseBody
    RestResponse<OrderMainVO> getDetail(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.getDetail(primaryOrderRestQuery));
    }

    @ApiOperation("订单详情(传primaryOrderIdList列表)")
    @PostMapping(name = "getDetail", value = "/api/trade/order/getDetailNew/token")
    public @ResponseBody
    RestResponse<List<OrderMainVO>> getDetailNew(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.getDetailNew(primaryOrderRestQuery));
    }

    @ApiOperation("状态流转详情")
    @PostMapping(name = "getFlow", value = "/api/trade/order/getFlow/token")
    public @ResponseBody
    RestResponse<List<OrderOperateFlowVO>> getFlow(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.getFlowList(primaryOrderRestQuery));
    }

    @ApiOperation("订单列表")
    @PostMapping(name = "getList", value = "/api/trade/order/getList/token")
    public @ResponseBody
    RestResponse<PageInfo<OrderMainVO>> getList(@RequestBody OrderListRestQuery orderListRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.getList(orderListRestQuery));
    }

    @ApiOperation("取消订单")
    @PostMapping(name = "cancelOrder", value = "/api/trade/order/cancelOrder/token")
    public @ResponseBody
    RestResponse<Boolean> cancelOrder(@RequestBody PrimaryOrderRestCommand primaryOrderRestCommand) {
        return RestResponse.ok(orderFacade.cancelOrder(primaryOrderRestCommand));
    }

    @ApiOperation("删除订单")
    @PostMapping(name = "delOrder", value = "/api/trade/order/delOrder/token")
    public @ResponseBody
    RestResponse<Boolean> delOrder(@RequestBody PrimaryOrderRestCommand primaryOrderRestCommand) {
        return RestResponse.ok(orderFacade.delOrder(primaryOrderRestCommand));
    }

    @ApiOperation("确认收货")
    @PostMapping(name = "confirmReceipt", value = "/api/trade/order/confirmReceipt/token")
    public @ResponseBody
    RestResponse<Boolean> confirmReceipt(@RequestBody PrimaryOrderRestCommand primaryOrderRestCommand) {
        return RestResponse.ok(orderFacade.confirmReceipt(primaryOrderRestCommand));
    }

    @ApiOperation("多阶段用户确认")
    @PostMapping(name = "confirmStepOrder", value = "/api/trade/order/confirmStepOrder/token")
    public @ResponseBody
    RestResponse<Boolean> confirmStepOrder(@RequestBody StepOrderRestCommand stepOrderRestCommand) {
        return RestResponse.ok(orderFacade.confirmStepOrder(stepOrderRestCommand));
    }

    @ApiOperation("订单评价")
    @PostMapping(name = "addEvaluation", value = "/api/trade/order/addEvaluation/token")
    public @ResponseBody
    RestResponse<Boolean> addEvaluation(@RequestBody AddEvaluationRestCommand addEvaluationRestCommand) {
        return RestResponse.ok(orderFacade.addEvaluation(addEvaluationRestCommand));
    }

    @ApiOperation("订单分状态数量")
    @PostMapping(name = "count", value = "/api/trade/order/count/token")
    public @ResponseBody
    RestResponse<OrderCountByStatusVO> count(@RequestBody CountOrderRestQuery query) {
        query.setStatus(new ArrayList<>(ImmutableList
            .of(OrderStatusEnum.ORDER_WAIT_PAY.getCode(), OrderStatusEnum.ORDER_SENDED.getCode(),
                OrderStatusEnum.REVERSAL_DOING.getCode())));
        return RestResponse.okWithoutMsg(orderFacade.count(query));
    }

    @ApiOperation("查询主订单的物流列表")
    @PostMapping(name = "queryLogisticsList", value = "/api/trade/order/queryLogisticsList/token")
    public @ResponseBody
    RestResponse<List<LogisticsDetailVO>> queryLogisticsList(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        return RestResponse.okWithoutMsg(orderFacade.queryLogisticsList(primaryOrderRestQuery));
    }

    @ApiOperation("物流详情")
    @PostMapping(name = "getLogisticsDetail", value = "/api/trade/order/getLogisticsDetail/token")
    public @ResponseBody
    RestResponse<LogisticsDetailVO> getLogisticsDetail(@RequestBody PrimaryOrderRestQuery primaryOrderRestQuery) {
        // todo for remove
        List<LogisticsDetailVO> r = orderFacade.queryLogisticsList(primaryOrderRestQuery);
        return RestResponse.okWithoutMsg(CollectionUtils.isEmpty(r) ? null : r.get(0));
    }

    /**
     * 保存物流记录
     * @param orderPickCommand
     * @return
     */
    @ApiOperation("保存点击记录")
    @PostMapping(name = "saveUserLogisticsPick", value = "/api/trade/order/saveUserLogisticsPick/token")
    public @ResponseBody
    RestResponse<Boolean> saveUserLogisticsPick(@RequestBody OrderPickCommand orderPickCommand) {
        Boolean result = orderFacade.saveUserLogisticsPick(orderPickCommand);
        return RestResponse.okWithoutMsg(result);
    }


    @ApiOperation("取消订单V2")
    @PostMapping(name = "cancelV2Orders", value = "/api/trade/order/cancelOrders/V2/token")
    public @ResponseBody
    RestResponse<Boolean> cancelV2Orders(@RequestBody PrimaryOrderRestCommand primaryOrderRestCommand) {
        Boolean result =  orderFacade.cancelOrders(primaryOrderRestCommand);
        return RestResponse.ok(result);
    }

}