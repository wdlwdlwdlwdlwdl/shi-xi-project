package com.aliyun.gts.gmall.manager.front.media.service.impl;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoLabelRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoLabelDTO;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoLabelRelationsDTO;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoLabelReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoLabelRelationsReadFacade;
import com.aliyun.gts.gmall.center.media.api.facade.ShortVideoLabelWriteFacade;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoLabelQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.media.converter.ShortVideoLabelVOConverter;
import com.aliyun.gts.gmall.manager.front.media.service.VideoLabelService;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoLabelVO;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description 视频标签管理服务实现类
 * @author wang.yulin01
 * @version V1.0
 * @date 2023/4/13 15:52
 **/
@Service
@Slf4j
public class VideoLabelServiceImpl implements VideoLabelService {

    @Resource
    private ShortVideoLabelReadFacade readFacade;
    @Resource
    private ShortVideoLabelWriteFacade writeFacade;
    @Resource
    private ShortVideoLabelRelationsReadFacade labelRelationsReadFacade;
    @Resource
    ShortVideoLabelVOConverter converter;


    /**
     * 查询标签详情
     * @param  query 标签ID
     * @return
     */
    @Override
    public ShortVideoLabelVO queryDetail(CommonByIdQuery query) {
        RpcResponse<ShortVideoLabelDTO> response =  readFacade.queryById(query);
        return converter.dto2VO(response.getData());
    }

    /**
     * 新增/编辑视频标签
     * @param shortVideoLabelDTO
     * @return
     */
    @Override
    public Boolean saveOrUpdate(@RequestBody ShortVideoLabelRpcReq shortVideoLabelDTO) {
        if (Objects.isNull(shortVideoLabelDTO)) {
            return Boolean.FALSE;
        }
        // 如果传主键ID则更新
        if (Objects.nonNull(shortVideoLabelDTO.getId())) {
            //根据id更新数据
            return writeFacade.updateShortVideoLabel(shortVideoLabelDTO).getData();
        }
        if (writeFacade.createShortVideoLabel(shortVideoLabelDTO).getData() > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public PageInfo<ShortVideoLabelVO> listByPage(ShortVideoLabelQueryReq query) {
        RestResponse<PageInfo<ShortVideoLabelVO>> response = ResponseUtils.convertVOPageResponse(readFacade.page(query),
                converter::dto2VO, false);
        if (Objects.nonNull(response) && Objects.nonNull(response.getData())
                && CollectionUtils.isNotEmpty(response.getData().getList())) {
            // 添加关联视频数量
            ShortVideoLabelRelationsDTO numQuery = new ShortVideoLabelRelationsDTO();
            for (ShortVideoLabelVO item : response.getData().getList()) {
                numQuery.setLabelId(item.getId());
                // 从关联表中统计数据
                RpcResponse<Long> relatedVideoNum = labelRelationsReadFacade.countRelatedNum(numQuery);
                item.setRelatedVideoNum(relatedVideoNum.getData());
            }
        }
        return response.getData();
    }

    /**
     * 根据ID删除标签
     * @param query
     * @return
     */
    @Override
    public Boolean deleteById(CommonByIdQuery query) {
        RpcResponse<Boolean> response =  writeFacade.deleteShortVideoLabel(query);
        return response.getData();
    }
}
