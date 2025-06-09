package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gmall.manager.front.sourcing.vo.QuoteDetailVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingMaterialVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/28 18:25
 */
@Mapper(componentModel = "spring")
public interface SourcingMaterialConvert {

    /**
     * @param materialVo
     * @return
     */
    @Mappings(value = {
            @Mapping(target = "scMaterialId", source = "id"),
    })
    QuoteDetailVo material2Detail(SourcingMaterialVo materialVo);

}
