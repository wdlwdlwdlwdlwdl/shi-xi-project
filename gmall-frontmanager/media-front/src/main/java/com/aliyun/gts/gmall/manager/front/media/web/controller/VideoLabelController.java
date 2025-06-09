package com.aliyun.gts.gmall.manager.front.media.web.controller;

import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoLabelQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.media.service.VideoLabelService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 短视频标签管理
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/14 14:31
 **/
@RestController
@RequestMapping(value = "/media/label")
@Api(value = "短视频标签管理", tags = {"短视频标签管理"})
public class VideoLabelController {

    @Resource
    private VideoLabelService videoLabelService;

    @ApiOperation("根据ID查询短视频标签详情")
    @PostMapping(value = "/getById")
    public RestResponse<ShortVideoLabelVO> queryDetail(@RequestBody CommonByIdQuery query) {
        ShortVideoLabelVO vo = videoLabelService.queryDetail(query);
        return RestResponse.okWithoutMsg(vo);
    }

    @ApiOperation("分页查询短视频列表")
    @PostMapping(value = "/page")
    public RestResponse<PageInfo<ShortVideoLabelVO>> listByPage(@RequestBody ShortVideoLabelQueryReq query) {
        PageInfo<ShortVideoLabelVO> result = videoLabelService.listByPage(query);
        return RestResponse.okWithoutMsg(result);
    }
}
