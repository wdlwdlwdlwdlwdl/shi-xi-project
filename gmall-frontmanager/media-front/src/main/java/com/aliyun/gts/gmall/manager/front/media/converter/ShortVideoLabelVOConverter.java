package com.aliyun.gts.gmall.manager.front.media.converter;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoLabelDTO;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoLabelVO;
import org.mapstruct.Mapper;

/**
 * @author GTS
 * @date 2021/02/07
 */
@Mapper(componentModel = "spring")
public interface ShortVideoLabelVOConverter {

    /**
     * 从dto转换成vo
     *
     * @param dto
     * @return
     */
    ShortVideoLabelVO dto2VO(ShortVideoLabelDTO dto);
}