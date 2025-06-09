package com.aliyun.gts.gmall.manager.front.item.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.localcache.RelationPageLocalCacheManager;
import com.aliyun.gts.gmall.platform.promotion.common.query.GrGroupQuery;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * description: 商品分组
 *
 * @author hu.zhiyong
 * @date 2022/09/26 16:54
 **/
@Api(value = "商品分组", tags = {"item"})
@RestController
public class ItemGroupController {

    @Autowired
    private RelationPageLocalCacheManager relationPageLocalCacheManager;

    @RequestMapping(value = "/api/item/group/relationPage")
    public RestResponse<List<ItemDetailVO>> relationPage(@RequestBody GrGroupQuery query) {
        // 默认最多给20个
        query.setPage(new PageParam());
        return RestResponse.okWithoutMsg(relationPageLocalCacheManager.relationPage(query));
    }

}
