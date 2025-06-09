package com.aliyun.gts.gmall.manager.front.promotion.common.converter;

import com.aliyun.gts.gmall.center.promotion.common.query.PlayAwardQuery;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.AwardQuery;
import org.mapstruct.Mapper;

/**
 * @author: yuli
 * @data: 2023/3/29 17:18
 */
@Mapper(componentModel = "spring")
public interface AwardConverter {
    PlayAwardQuery toPlayAwardQuery(AwardQuery query);
}
