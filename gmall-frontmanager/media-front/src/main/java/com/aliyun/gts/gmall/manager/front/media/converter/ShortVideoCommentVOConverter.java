package com.aliyun.gts.gmall.manager.front.media.converter;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCommentDTO;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCommentVO;
import org.mapstruct.Mapper;

/**
 * @author GTS
 * @date 2021/02/07
 */
@Mapper(componentModel = "spring")
public interface ShortVideoCommentVOConverter {

    /**
     * 从dto转换成vo
     *
     * @param dto
     * @return
     */
    ShortVideoCommentVO dto2VO(ShortVideoCommentDTO dto);
}