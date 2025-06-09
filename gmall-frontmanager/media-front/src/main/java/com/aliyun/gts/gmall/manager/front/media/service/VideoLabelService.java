package com.aliyun.gts.gmall.manager.front.media.service;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoLabelRpcReq;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoLabelQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoLabelVO;

/**
 * @description 标签服务类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:30
 **/
public interface VideoLabelService {

    /**
     * 详情
     * @param  query 标签ID
     * @return
     */
    ShortVideoLabelVO queryDetail(CommonByIdQuery query);

    /**
     * 新增/编辑短视频标签
     * @param shortVideoLabelDTO
     * @return
     */
    Boolean saveOrUpdate(ShortVideoLabelRpcReq shortVideoLabelDTO);

    PageInfo<ShortVideoLabelVO> listByPage(ShortVideoLabelQueryReq query);

    /**
     * 根据ID删除标签
     * @param query
     * @return
     */
    Boolean deleteById(CommonByIdQuery query);
}
