package com.aliyun.gts.gmall.manager.front.item.convertor;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropGroupVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.PropValueVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryPropGroupDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.category.CatPropValueDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.category.CategoryPropertyDTO;

@Mapper(componentModel = "spring")
public abstract class CategoryPropGroupConvertor {
    @Autowired
    protected MultiLangConverter multiLangConverter;

    // ItemCatPropGroupVO <---> CategoryPropGroupDTO
    @Mapping(target = "groupName", expression = "java(multiLangConverter.mText_to_str(source.getGroupName()))")
    @Mapping(target = "categoryPropList", ignore = true)
    public abstract ItemCatPropGroupVO toItemCatPropGroupVo(CategoryPropGroupDTO source);

    public abstract List<ItemCatPropGroupVO> toItemCatPropGroupVos(List<CategoryPropGroupDTO> list);

    // ItemCatPropValueVO <---> CategoryPropertyDTO
    @Mapping(target = "propName", expression = "java(multiLangConverter.mText_to_str(source.getPropName()))")
    public abstract ItemCatPropValueVO toItemCatPropValueVo(CategoryPropertyDTO source);

    // PropValueVO <---> CatPropValueDTO
    @Mapping(target = "value", expression = "java(multiLangConverter.mText_to_str(source.getPropValue()))")
    public abstract PropValueVO toPropValueVo(CatPropValueDTO source);

    public abstract List<PropValueVO> toPropValueVos(List<CatPropValueDTO> list);
}
