package com.aliyun.gts.gmall.manager.front.media.service;

import com.aliyun.gts.gmall.center.media.api.dto.output.AcsInfo;
import com.aliyun.gts.gmall.center.media.api.dto.output.Result;
import com.aliyun.gts.gmall.center.media.api.dto.output.VideoInfoDTO;

/**
 * @description 短视频VOD管理服务
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:30
 **/
public interface MediaVodService {

    Result<VideoInfoDTO> getUploadAddrAndAuth(String title, String fileName);

    Result<VideoInfoDTO> refreshUploadAddrAndAuth(String videoId);

    Result<AcsInfo> getAcsInfo();

    Result<VideoInfoDTO> getVideoPlayAuth(String videoId, Long authInfoTimeOut);
}
