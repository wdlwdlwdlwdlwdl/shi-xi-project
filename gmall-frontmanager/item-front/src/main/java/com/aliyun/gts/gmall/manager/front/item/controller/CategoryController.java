package com.aliyun.gts.gmall.manager.front.item.controller;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryAndPropVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.CategoryAllByParamV2Query;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryTreeVO;
import com.aliyun.gts.gmall.manager.front.item.facade.CategoryFacade;

/**
 * 
 * @Title: CategoryController.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2024年10月29日 16:08:12
 * @version V1.0
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryFacade categoryFacade;

    /**
     * 查询所有类目
     * 
     * @param req
     * @return
     */
    @PostMapping("/all")
    public RestResponse<List<CategoryTreeVO>> page(@RequestBody CategoryAllByParamV2Query req) {
        return RestResponse.okWithoutMsg(categoryFacade.queryAllByParam(req));
    }

    /**
     * 查询类目树，类目属性
     * 
     * @param req 类目ID
     * @return VO
     */
    @PostMapping("/queryCategoryList")
    public @ResponseBody RestResponse<CategoryAndPropVO> queryCategoryList(@RequestBody CategoryRestQuery req) {
        return RestResponse.okWithoutMsg(categoryFacade.queryCategoryList(req));
    }
}
