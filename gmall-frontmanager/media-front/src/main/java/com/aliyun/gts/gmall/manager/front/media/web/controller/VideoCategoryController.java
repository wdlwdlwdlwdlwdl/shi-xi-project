package com.aliyun.gts.gmall.manager.front.media.web.controller;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCategoryRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCategoryQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.media.service.VideoCategoryService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/5 13:55
 */
@RestController
@RequestMapping(value = "/media/category")
@Api(value = "短视频分类操作", tags = {"短视频分类操作"})
@Slf4j
public class VideoCategoryController {

    @Resource
    private VideoCategoryService categoryService;

    @ApiOperation("短视频分类详情查询")
    @PostMapping(value = "/getById")
    public RestResponse<ShortVideoCategoryVO> queryDetail(@RequestBody CommonByIdQuery query) {
        ShortVideoCategoryVO vo = categoryService.queryDetail(query);
        return RestResponse.okWithoutMsg(vo);
    }

    @ApiOperation("分页查询短视频分类")
    @PostMapping(value = "/page")
    public RestResponse<PageInfo<ShortVideoCategoryVO>> listByPage(@RequestBody ShortVideoCategoryQueryReq query) {
        PageInfo<ShortVideoCategoryVO> result = categoryService.listByPage(query);
        return RestResponse.okWithoutMsg(result);
    }

    @ApiOperation("查询APP、H5分类列表")
    @PostMapping(value = "/selectAll4TooBar")
    public RestResponse<List<ShortVideoCategoryDTO>> selectAll4TooBar() {
        log.info("VideoCategoryController#selectAll4TooBar start");
        return RestResponse.okWithoutMsg(categoryService.selectAll());
    }
}
