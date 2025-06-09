package com.aliyun.gts.gmall.manager.front.media.service;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCategoryRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCategoryQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCategoryVO;

import java.util.List;

/**
 * @description 短视频分类服务
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:30
 **/
public interface VideoCategoryService {

    /**
     * 详情
     * @param  query 分类ID
     * @return
     */
    ShortVideoCategoryVO queryDetail(CommonByIdQuery query);

    /**
     * 新增/编辑短视频分类
     * @param shortVideoCategoryDTO
     * @return
     */
    Boolean saveOrUpdate(ShortVideoCategoryRpcReq shortVideoCategoryDTO);

    PageInfo<ShortVideoCategoryVO> listByPage(ShortVideoCategoryQueryReq query);

    /**
     * 根据ID删除标签
     * @param query
     * @return
     */
    Boolean deleteById(CommonByIdQuery query);

    List<ShortVideoCategoryDTO> selectAll();
}
