package com.aliyun.gts.gmall.manager.front.media.service.impl;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoCategoryRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCategoryReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoCategoryWriteFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoInfoReadFacade;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoCategoryQueryReq;
import com.aliyun.gts.gmall.center.media.common.enums.CategoryStatusEnum;
import com.aliyun.gts.gmall.center.media.common.enums.StatusEnum;
import com.aliyun.gts.gmall.center.media.common.enums.VideoStatusEnum;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.media.converter.ShortVideoCategoryVOConverter;
import com.aliyun.gts.gmall.manager.front.media.service.VideoCategoryService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCategoryVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 视频分类管理服务实现类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:52
 **/
@Service
@Slf4j
public class VideoCategoryServiceImpl implements VideoCategoryService {

    @Resource
    private ShortVideoCategoryReadFacade readFacade;
    @Resource
    private ShortVideoCategoryWriteFacade writeFacade;
    @Resource
    private ShortVideoInfoReadFacade infoReadFacade;
    @Resource
    ShortVideoCategoryVOConverter converter;


    /**
     * 查询分类详情
     * @param  query 分类ID
     * @return
     */
    @Override
    public ShortVideoCategoryVO queryDetail(CommonByIdQuery query) {
        RpcResponse<ShortVideoCategoryDTO> response =  readFacade.queryById(query);
        return converter.dto2VO(response.getData());
    }

    /**
     * 新增/编辑视频分类
     * @param shortVideoCategoryDTO
     * @return
     */
    @Override
    public Boolean saveOrUpdate(@RequestBody ShortVideoCategoryRpcReq shortVideoCategoryDTO) {
        if (Objects.isNull(shortVideoCategoryDTO)) {
            return Boolean.FALSE;
        }
        // 如果传主键ID则更新
        if (Objects.nonNull(shortVideoCategoryDTO.getId())) {
            //分类状态：只有关联视频为0才能下线
            if (Objects.nonNull(shortVideoCategoryDTO.getCategoryStatus())
                    && CategoryStatusEnum.OFF_LINE.getCode().equals(shortVideoCategoryDTO.getCategoryStatus())) {
                //根据ID查询关联视频数量
                ShortVideoInfoDTO numQuery = new ShortVideoInfoDTO();
                numQuery.setCategoryId(shortVideoCategoryDTO.getId());
                RpcResponse<Long> relatedVideoNum = infoReadFacade.countRelatedNum(numQuery);
                if (Objects.nonNull(relatedVideoNum) && Objects.nonNull(relatedVideoNum.getData())
                        && relatedVideoNum.getData().compareTo(0L) > 0) {
                    return Boolean.FALSE;
                }
            }
            //根据id更新数据
            return writeFacade.updateShortVideoCategory(shortVideoCategoryDTO).getData();
        }
        if (writeFacade.createShortVideoCategory(shortVideoCategoryDTO).getData() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public PageInfo<ShortVideoCategoryVO> listByPage(ShortVideoCategoryQueryReq query) {
        RestResponse<PageInfo<ShortVideoCategoryVO>> response = ResponseUtils.convertVOPageResponse(readFacade.page(query),
                converter::dto2VO, false);
        if (Objects.nonNull(response) && Objects.nonNull(response.getData())
                && CollectionUtils.isNotEmpty(response.getData().getList())) {

            ShortVideoInfoDTO numQuery = new ShortVideoInfoDTO();
            for (ShortVideoCategoryVO item : response.getData().getList()) {
                // 添加关联视频数量
                numQuery.setCategoryId(item.getId());
                RpcResponse<Long> relatedVideoNum = infoReadFacade.countRelatedNum(numQuery);
                item.setRelatedVideoNum(relatedVideoNum.getData());
                // 添加关联正常视频数量
                numQuery.setVideoStatus(VideoStatusEnum.DISTRIBUTED.getCode());
                item.setRelatedNormalVideoNum(infoReadFacade.countRelatedNum(numQuery).getData());
                //枚举转换
                item.setCategoryStatusName(CategoryStatusEnum.getName(item.getCategoryStatus()));
                item.setStatusName(StatusEnum.getName(item.getDeleted()));
            }
        }
        return response.getData();
    }

    /**
     * 根据ID删除分类
     * @param query
     * @return
     */
    @Override
    public Boolean deleteById(CommonByIdQuery query) {
        // 查询分类状态，只有在下线（20）状态才能删除
        RpcResponse<ShortVideoCategoryDTO> response =  readFacade.queryById(query);
        if (Objects.isNull(response) || Objects.isNull(response.getData())
                || !CategoryStatusEnum.OFF_LINE.getCode().equals(response.getData().getCategoryStatus())) {
            return Boolean.FALSE;
        }

        return   writeFacade.deleteShortVideoCategory(query).getData();
    }

    @Override
    public List<ShortVideoCategoryDTO> selectAll() {
        log.info("VideoCategoryServiceImpl#selectAll视频分类查询开始");
        List<ShortVideoCategoryDTO> list = readFacade.selectAll4TooBar().getData();
        log.info("VideoCategoryServiceImpl#selectAll 结果：", list);
        return CollectionUtils.isEmpty(list) ? new ArrayList<>() : list;
    }
}
