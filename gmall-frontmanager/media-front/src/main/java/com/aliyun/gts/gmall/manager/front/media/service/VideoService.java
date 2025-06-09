package com.aliyun.gts.gmall.manager.front.media.service;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoInfoRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoLikesRpcReq;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoInfoQueryReq;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustShopInterestRelRpcReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.manager.front.media.dto.ShortVideoInfoQuery;
import com.aliyun.gts.gmall.manager.front.media.dto.VideoInitializeQuery;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoVO;
import com.aliyun.gts.gmall.manager.front.media.web.output.VideoInitializeInfoVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @description 短视频管理服务
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:30
 **/
public interface VideoService {

    /**
     * 详情
     * @param  query 视频ID
     * @return
     */
    ShortVideoVO queryDetail(CommonByIdQuery query);


    PageInfo<ShortVideoVO> listByPage(ShortVideoInfoQueryReq query);

    /**
     * 更新视频状态
     * @param query
     * @return
     */
    Boolean updateStatus(ShortVideoInfoRpcReq query);

    /**
     * 更新评论状态
     * @param query
     * @return
     */
    Boolean updateCommentStatus(ShortVideoInfoRpcReq query);

    /**
     * 新增或编辑短视频
     * @param shortVideoDTO
     * @return
     */
    Boolean saveOrUpdate(ShortVideoInfoRpcReq shortVideoDTO);

    /**
     * 根据ID删除
     * @param query
     * @return
     */
    Boolean deleteById(CommonByIdQuery query);

    /**
     * 点赞/取消赞
     * @param query
     * @return
     */
    Boolean addOrCancelLikes(ShortVideoLikesRpcReq query);

    /**
     * 查询下一条视频
     * @param query
     * @return
     */
    ShortVideoVO queryNextVideo(ShortVideoInfoQueryReq query);

    Boolean cancelInterest(CommonByIdQuery query);

    Long addInterest(CustShopInterestRelRpcReq custShopInterestRelDTO);

    VideoInitializeInfoVO getVideoInitializeInfo(VideoInitializeQuery query);

    Integer share(CommonByIdQuery query);
}
