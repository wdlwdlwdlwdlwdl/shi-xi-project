package com.aliyun.gts.gmall.manager.front.controller;


import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.input.PrintLogReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志打印
 * @anthor shifeng
 * @version 1.0.1
 * 2025-1-21 15:51:33
 */
@Slf4j
@RestController
@RequestMapping(value = "/print")
public class PrintLogController {

    /**
     * 打印前端日志
     * @param printLogReq
     * @return
     */
    @PostMapping("/log")
    public RestResponse<Boolean> log(@RequestBody PrintLogReq printLogReq) {
        log.warn("PrintLogController logs {}",  printLogReq.getPrintLog());
        return RestResponse.okWithoutMsg(Boolean.TRUE);
    }
}
