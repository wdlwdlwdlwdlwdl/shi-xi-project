package com.aliyun.gts.gmall.manager.front.promotion.common.converter;

import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayActivityDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayPrizeRelateDTO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayPrizeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Date;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/13 19:18
 */
@Mapper(componentModel = "spring")
public interface PlayActivityConvert {
    /**
     *
     * @param dto
     * @return
     */
    @Mappings({
            @Mapping(source = "prizes", target = "prizes", ignore = true),
            @Mapping(target = "remainTime",expression = "java(computeTime(dto.getEndTime()))" ),
    })
    PlayActivityVO dto2Vo(PlayActivityDTO dto);

    PlayPrizeVo  dto2Vo(PlayPrizeRelateDTO prizes);

    default Long computeTime(Date endTime){
        Date now = new Date();
        return endTime.getTime() - now.getTime();
    }
}
