package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/5 13:55
 */
@RestController
@RequestMapping(value = "/promotion/account")
public class TestController extends BaseRest {
    @RequestMapping(value = "/test")
    public RestResponse<String> test() {
        return RestResponse.ok("OK");
    }
}
