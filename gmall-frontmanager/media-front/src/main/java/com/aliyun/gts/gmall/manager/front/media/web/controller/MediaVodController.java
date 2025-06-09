package com.aliyun.gts.gmall.manager.front.media.web.controller;

import com.aliyun.gts.gmall.center.media.api.dto.output.AcsInfo;
import com.aliyun.gts.gmall.center.media.api.dto.output.Result;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.dto.output.VideoInfoDTO;
import com.aliyun.gts.gmall.manager.front.media.service.MediaVodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description 短视频vod管理
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/14 14:31
 **/
@Api(value = "短视频VOD操作", tags = {"短视频VOD操作"})
@RestController
@RequestMapping(value = "/media/vod")
public class MediaVodController {

    @Resource
    private MediaVodService vodService;

    @ApiOperation("获取上传凭证")
    @RequestMapping(value="/getVideoUploadAddrAndAuth", method = {RequestMethod.POST})
    public Result<VideoInfoDTO> getVideoUpLoadAddrAndAuth(@RequestBody ShortVideoInfoDTO shortVideoInforDTO) {
        Result<VideoInfoDTO> result = vodService.getUploadAddrAndAuth(shortVideoInforDTO.getName(), shortVideoInforDTO.getFileName());
        return result;
    }

    @ApiOperation("刷新上传凭证")
    @RequestMapping(value = "/refreshVideoUploadAddrAndAuth",method = {RequestMethod.POST})
    public Result<VideoInfoDTO> refreshVideoUploadAddrAndAuth(@RequestBody ShortVideoInfoDTO shortVideoInforDTO) {
        return vodService.refreshUploadAddrAndAuth(shortVideoInforDTO.getCode());
    }

    @ApiOperation("获取Acs信息")
    @RequestMapping(value = "/getAcsInfo",method = {RequestMethod.POST})
    public Result<AcsInfo> getAcsInfo() {
        return vodService.getAcsInfo();
    }

    @ApiOperation("获取播放凭证")
    @RequestMapping(value = "/getVideoPlayAuth",method = {RequestMethod.POST})
    public Result<VideoInfoDTO> getVideoPlayAuth(@RequestBody ShortVideoInfoDTO shortVideoInforDTO) {
        return vodService.getVideoPlayAuth(shortVideoInforDTO.getCode(), shortVideoInforDTO.getAuthInfoTimeOut());
    }
}
