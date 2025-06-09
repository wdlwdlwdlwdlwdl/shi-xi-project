package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.BidingPriceDTO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.BidingPriceVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/3 16:27
 */
@Mapper(componentModel = "spring",imports = {PriceUtils.class})
public interface BidingPriceConvert {
    @Mappings(value = {
            @Mapping(target = "totalPrice", expression = "java(PriceUtils.fenToYuan(dto.getTotalPrice()))"),
            @Mapping(target = "totalPriceFen", source = "totalPrice"),
            @Mapping(target = "firstPriceFen", source = "firstPrice"),
    })
    BidingPriceVo dto2Vo(BidingPriceDTO dto);
}
