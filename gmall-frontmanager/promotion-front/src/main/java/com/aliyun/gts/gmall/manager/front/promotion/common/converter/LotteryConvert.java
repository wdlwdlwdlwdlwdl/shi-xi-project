package com.aliyun.gts.gmall.manager.front.promotion.common.converter;

import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayAwardDTO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayAwardVO;
import org.mapstruct.Mapper;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/15 10:11
 */
@Mapper(componentModel = "spring")
public interface LotteryConvert {

    PlayAwardVO dto2Vo(PlayAwardDTO dto);
}
