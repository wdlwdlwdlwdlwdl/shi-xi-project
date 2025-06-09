package com.aliyun.gts.gmall.manager.front.media.service.impl;

import com.aliyun.gts.gmall.center.media.api.dto.output.AcsInfo;
import com.aliyun.gts.gmall.center.media.api.dto.output.Result;
import com.aliyun.gts.gmall.center.media.api.dto.output.VideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.facade.VodFacade;
import com.aliyun.gts.gmall.manager.front.media.service.MediaVodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 短视频VOD管理服务实现类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:52
 **/
@Service
@Slf4j
public class MediaVodServiceImpl implements MediaVodService {

    @Resource
    private VodFacade vodFacade;


    @Override
    public Result<VideoInfoDTO> getUploadAddrAndAuth(String title, String fileName) {

        return vodFacade.getUploadAddrAndAuth(title, fileName);
    }

    @Override
    public Result<VideoInfoDTO> refreshUploadAddrAndAuth(String videoId) {
        return vodFacade.refreshUploadAddrAndAuth(videoId);
    }

    @Override
    public Result<AcsInfo> getAcsInfo() {
        return vodFacade.getAcsInfo();
    }

    @Override
    public Result<VideoInfoDTO> getVideoPlayAuth(String videoId, Long authInfoTimeOut) {
        return vodFacade.getVideoPlayAuth(videoId, authInfoTimeOut);
    }
}
