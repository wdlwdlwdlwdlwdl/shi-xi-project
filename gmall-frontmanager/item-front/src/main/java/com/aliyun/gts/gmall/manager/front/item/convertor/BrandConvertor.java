package com.aliyun.gts.gmall.manager.front.item.convertor;

import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.output.BrandVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropGroupVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.brand.BrandDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryPropGroupDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BrandConvertor {
    @Autowired
    protected MultiLangConverter multiLangConverter;


    public abstract List<BrandVO> toBrandVOList(List<BrandDTO>list);

    @Mapping(target = "name", expression = "java(multiLangConverter.mText_to_str(target.getName()))")
    public abstract BrandVO toBrandVO(BrandDTO target);


}
