package com.aliyun.gts.gmall.manager.front.media.converter;

import org.mapstruct.Mapper;

import com.aliyun.gts.gmall.center.media.api.dto.input.ShortVideoInfoRpcReq;
import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoInfoDTO;
import com.aliyun.gts.gmall.center.media.api.input.query.ShortVideoInfoQueryReq;
import com.aliyun.gts.gmall.manager.front.media.dto.ShortVideoInfoQuery;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoVO;

/**
 * @author GTS
 * @date 2021/02/07
 */
@Mapper(componentModel = "spring")
public interface ShortVideoConverter {

    /**
     * 从dto转换成vo
     *
     * @param dto
     * @return
     */
    ShortVideoVO dto2VO(ShortVideoInfoDTO dto);

    ShortVideoInfoRpcReq dto2Req (ShortVideoInfoDTO dto);

    ShortVideoInfoQueryReq query2Req(ShortVideoInfoQuery query);
}