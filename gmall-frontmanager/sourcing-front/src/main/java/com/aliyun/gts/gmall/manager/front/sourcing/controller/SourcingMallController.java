package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SourcingMallQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/sourcing")
public class SourcingMallController {
    @Resource
    private SourcingFacade sourcingFacade;

    @RequestMapping(value = "/page")
    public RestResponse<PageInfo<SourcingVo>> page(@RequestBody SourcingMallQueryReq query) {
        RestResponse<PageInfo<SourcingVo>> resp = sourcingFacade.page(query.build());
        if (resp.isSuccess() && resp.getData() != null) {
            sourcingFacade.fillDetail(resp.getData().getList());
        }
        return resp;
    }
}
