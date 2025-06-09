package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.login.adaptor.WeiXinAdapter;
import com.aliyun.gts.gmall.manager.front.login.dto.input.wechat.ShortLinkReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "微信相关接口")
@RestController
@RequestMapping(value = "/api/wechat")
public class WeChatController {

    private final WeiXinAdapter weiXinAdapter;

    public WeChatController(WeiXinAdapter weiXinAdapter) {
        this.weiXinAdapter = weiXinAdapter;
    }

    @ApiOperation("获取微信小程序短链")
    @PostMapping(value = "/short/link")
    public RestResponse<String> getShortLink(@RequestBody ShortLinkReq req) {
//        Long custId = UserHolder.getUser().getCustId();
//        if (custId == null) {
//            return RestResponse.fail(LoginFrontResponseCode.GET_SHORT_LINK_USER_TOKEN_FAIL);
//        }
        return RestResponse.okWithoutMsg(weiXinAdapter.getShortLink(req));
    }

}
