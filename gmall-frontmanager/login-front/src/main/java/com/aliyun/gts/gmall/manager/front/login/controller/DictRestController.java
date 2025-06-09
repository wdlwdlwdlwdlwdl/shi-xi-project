package com.aliyun.gts.gmall.manager.front.login.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.permission.Permission;
import com.aliyun.gts.gmall.manager.biz.adapter.DictAdapter;
import com.aliyun.gts.gmall.manager.biz.output.DictVO;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.DictQueryByKeyRestReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dict")
public class DictRestController {

    @Autowired
    private DictAdapter dictAdapter;


    @RequestMapping("/queryByKey")
    @Permission(required = false)
    public RestResponse<DictVO> queryByKey(@RequestBody DictQueryByKeyRestReq req) {
        DictVO dictVO = dictAdapter.queryByKey(req.getDictKey());
        return RestResponse.okWithoutMsg(dictVO);
    }



}
