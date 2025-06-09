package com.aliyun.gts.gmall.manager.front.item.controller;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ByPidRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.RegionVO;
import com.aliyun.gts.gmall.manager.front.item.facade.CommonRegionFacade;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gshine
 * @since 2/24/21 10:40 AM
 */
@Api(value = "地址信息分层获取", tags = {"region"})
@RestController
public class RegionRestController {

    @Autowired
    private CommonRegionFacade regionService;

    @RequestMapping("/api/item/region/getByParentId")
    public RestResponse<List<RegionVO>> getRegionsByParentId(@RequestBody ByPidRestQuery query) {
        return regionService.queryRegionsByParentId(query.getParentId());
    }
}
