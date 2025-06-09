package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;
import com.aliyun.gts.gmall.manager.front.trade.facade.ReasonFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 订单操作
 *
 * @author tiansong
 */
@Api(value = "取消原因", tags = {"trade", "order", "token"})
@RestController
public class ReasonController {

    @Autowired
    private ReasonFacade reasonFacade;

    @ApiOperation("取消原因列表")
    @PostMapping(name = "queryCancelReason", value = "/api/trade/queryCancelReason/token")
    public @ResponseBody
    RestResponse<PageInfo<CancelReasonVO>> queryCancelReason(@RequestBody CancelReasonQueryReq req) {
        return RestResponse.okWithoutMsg(reasonFacade.queryCancelReasonList(req));
    }

}