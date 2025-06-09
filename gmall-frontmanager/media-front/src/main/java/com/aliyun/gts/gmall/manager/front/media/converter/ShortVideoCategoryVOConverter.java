package com.aliyun.gts.gmall.manager.front.media.converter;

import com.aliyun.gts.gmall.center.media.api.dto.output.ShortVideoCategoryDTO;
import com.aliyun.gts.gmall.manager.front.media.web.output.ShortVideoCategoryVO;
import org.mapstruct.Mapper;

/**
 * @author GTS
 * @date 2021/02/07
 */
@Mapper(componentModel = "spring")
public interface ShortVideoCategoryVOConverter {

    /**
     * 从dto转换成vo
     *
     * @param dto
     * @return
     */
    ShortVideoCategoryVO dto2VO(ShortVideoCategoryDTO dto);
}