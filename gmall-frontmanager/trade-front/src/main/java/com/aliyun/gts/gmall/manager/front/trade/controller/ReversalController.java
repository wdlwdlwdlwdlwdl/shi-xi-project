package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.CreateReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ModifyReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalBuyerConfirmRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalDeliverRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalDetailRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal.ReversalOrderVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.ReversalFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 售后单操作
 *
 * @author tiansong
 */
@Api(value = "售后单操作", tags = {"trade", "reversal", "token"})
@RestController
public class ReversalController {

    @Autowired
    private ReversalFacade reversalFacade;

    @ApiOperation("售后单申请查询")
    @PostMapping(name = "queryForCreate", value = "/api/trade/reversal/queryForCreate/token")
    public @ResponseBody
    RestResponse<ReversalOrderDetailVO> queryForCreate(@RequestBody ReversalCheckRestQuery reversalCheckRestQuery) {
        return RestResponse.okWithoutMsg(reversalFacade.queryForCreate(reversalCheckRestQuery));
    }

    @ApiOperation("售后单创建")
    @PostMapping(name = "create", value = "/api/trade/reversal/create/token")
    public @ResponseBody
    RestResponse<Long> create(@RequestBody CreateReversalRestCommand createReversalRestCommand) {
        return RestResponse.ok(reversalFacade.create(createReversalRestCommand));
    }

    @ApiOperation("售后单取消")
    @PostMapping(name = "cancel", value = "/api/trade/reversal/cancel/token")
    public @ResponseBody
    RestResponse<Boolean> cancel(@RequestBody ModifyReversalRestCommand modifyReversalRestCommand) {
        return RestResponse.ok(reversalFacade.cancel(modifyReversalRestCommand));
    }

    @ApiOperation("售后单发货提交")
    @PostMapping(name = "sendDeliver", value = "/api/trade/reversal/sendDeliver/token")
    public @ResponseBody
    RestResponse<Boolean> sendDeliver(@RequestBody ReversalDeliverRestCommand reversalDeliverRestCommand) {
        return RestResponse.ok(reversalFacade.sendDeliver(reversalDeliverRestCommand));
    }

    @ApiOperation("售后单详情")
    @PostMapping(name = "queryDetail", value = "/api/trade/reversal/queryDetail/token")
    public @ResponseBody
    RestResponse<ReversalDetailVO> queryDetail(@RequestBody ReversalDetailRestQuery reversalDetailRestQuery) {
        return RestResponse.okWithoutMsg(reversalFacade.queryDetail(reversalDetailRestQuery));
    }

    @ApiOperation("售后单列表")
    @PostMapping(name = "queryList", value = "/api/trade/reversal/queryList/token")
    public @ResponseBody
    RestResponse<PageInfo<ReversalOrderVO>> queryList(@RequestBody ReversalRestQuery reversalRestQuery) {
        return RestResponse.okWithoutMsg(reversalFacade.queryList(reversalRestQuery));
    }

    @ApiOperation("买家确认退款")
    @PostMapping(name = "confirmRefund", value = "/api/trade/pay/confirmRefund/token")
    public @ResponseBody
    RestResponse<Boolean> confirmRefund(@RequestBody ReversalBuyerConfirmRestCommand command) {
        return RestResponse.okWithoutMsg(reversalFacade.buyerConfirmRefund(command));
    }
}