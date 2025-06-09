package com.aliyun.gts.gmall.manager.front.customer.controller;



import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.IpUtil;
import com.aliyun.gts.gmall.framework.api.util.JsonUtil;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.TrackingLogAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.TrackingLogCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 跟踪日志
 */
@RestController
@RequestMapping("/api/tracking")
@Slf4j
public class TrackingLogController {

    @Autowired
    TrackingLogAdapter trackingLogAdapter;

    @GetMapping("/addLog")
    public RestResponse<Boolean> addLog(@RequestParam Map<String, String> allParams, HttpServletRequest httpServletRequest) {
        log.info("add tracking log ===> " + JsonUtil.getIndentJsonString(allParams));
        TrackingLogCommand cmd = new TrackingLogCommand();
        cmd.setEvent(allParams.get("event"));
        cmd.setEnv(allParams.get("env"));
        cmd.setTraceId(allParams.get("traceId") == null ? allParams.get("uuid") : allParams.get("traceId"));
        cmd.setSite(allParams.get("site"));
        cmd.setSpm(allParams.get("spm"));
        cmd.setScm(allParams.get("scm"));
        cmd.setPage(allParams.get("page"));
        cmd.setInstance(allParams.get("instance"));
        cmd.setItemId(allParams.get("itemId") == null ? allParams.get("id") : allParams.get("itemId"));
        cmd.setOrderId(allParams.get("orderId"));
        CustDTO custDTO = UserHolder.getUser();
        if (custDTO != null) {
            cmd.setLogin("Y");
            cmd.setUserId(custDTO.getCustId().toString());
        }
        else {
            cmd.setLogin("N");
        }

        //这个小程序用的，没有用
        cmd.setTid(allParams.get("tid"));
        //前端没搜到，应该是没有
        cmd.setTradeNo(allParams.get("tradeNo"));
        //补充ip
        cmd.setClientIp(IpUtil.getIP(httpServletRequest));
        cmd.setGmtCreate(new Date());
        return RestResponse.okWithoutMsg(trackingLogAdapter.addLog(cmd));
    }
}
