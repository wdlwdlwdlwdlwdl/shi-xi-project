package com.aliyun.gts.gmall.manager.front.item.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.BrandIdsQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.BrandVO;
import com.aliyun.gts.gmall.manager.front.item.facade.BrandFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @Autowired
    private BrandFacade brandFacade;

    /**
     * 查询品牌
     *
     * @param req  ids
     * @return list
     */
    @PostMapping("/brandList")
    public RestResponse<List<BrandVO>> page(@RequestBody BrandIdsQuery req) {
        return RestResponse.okWithoutMsg(brandFacade.queryAllByParam(req));
    }
}
