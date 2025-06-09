package com.aliyun.gts.gmall.manager.front.media.service;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCommentRpcReq;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCommentQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCommentVO;

/**
 * @description 评论服务类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:30
 **/
public interface VideoCommentService {

    PageInfo<ShortVideoCommentVO> listByPage(ShortVideoCommentQueryReq query, Long userId);

    /**
     * 根据ID删除
     * @param query
     * @return
     */
    Boolean deleteById(CommonByIdQuery query);

    Long addComment(ShortVideoCommentRpcReq shortVideoCommentDTO);
}
