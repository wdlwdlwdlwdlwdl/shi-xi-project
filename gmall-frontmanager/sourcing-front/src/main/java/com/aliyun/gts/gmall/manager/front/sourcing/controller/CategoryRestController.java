package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.controller.BaseRest;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByPidQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.CategoryQueryRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryNodeVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gshine
 * @since 2/24/21 2:16 PM
 */
@RestController
@RequestMapping("/api/category")
public class CategoryRestController extends BaseRest {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/queryListByParentId")
    public RestResponse<List<CategoryVO>> queryListByParentId(@RequestBody ByPidQueryRestReq req) {
        List<CategoryVO> list = categoryService.queryListByParentId(req);
        return RestResponse.okWithoutMsg(list);
    }

    @RequestMapping("/queryTreeByParams")
    public RestResponse<List<CategoryNodeVO>> queryTreeByParams(@RequestBody CategoryQueryRestReq req) {
        return RestResponse.okWithoutMsg(categoryService.queryTreeByParams(req));
    }
}
