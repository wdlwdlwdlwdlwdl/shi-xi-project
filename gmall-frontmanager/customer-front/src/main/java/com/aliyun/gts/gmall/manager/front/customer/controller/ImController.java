package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ImSellerQuery;
import com.aliyun.gts.gmall.manager.front.customer.facade.ImLoginReadFacade;
import com.aliyun.gts.gmall.platform.gim.common.query.MessageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/8/11 10:13
 */
@RestController
@Api(value = "im聊天相关操作", tags = {"im"})
@Slf4j
public class ImController {
    @Resource
    private ImLoginReadFacade imLoginReadFacade;

    @ApiOperation(value = "查询客服服务地址")
    @PostMapping(name = "queryCCO", value = "/api/customer/queryCCOIm")
    @ResponseBody
    public RestResponse<String> queryCCOIm(@RequestBody ImSellerQuery login) {
        Long uid = null;
        if (login.getSellerId() != null) {
            uid = imLoginReadFacade.queryCCOImUid(login.getSellerId());
        }
        return imLoginReadFacade.queryImChatUrl(login.getCustId(), uid, login.getTargetType());
    }

    @ApiOperation(value = "查询客服未读消息数")
    @RequestMapping(value = "/api/customer/queryCCOUnRead")
    public RestResponse<Long> queryCCOUnRead(@RequestBody LoginRestQuery loginRestQuery) {
        Long custId = loginRestQuery.getCustId();
        Long uid = imLoginReadFacade.getCustImUid(custId);
        MessageQuery query = new MessageQuery();
        query.setReceiveId(uid);
        Long unRead = imLoginReadFacade.countUnRead(query);
        return RestResponse.okWithoutMsg(unRead);
    }
}
