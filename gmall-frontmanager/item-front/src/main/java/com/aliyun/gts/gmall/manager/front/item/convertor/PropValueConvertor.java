package com.aliyun.gts.gmall.manager.front.item.convertor;


import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.PropValueVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.property.PropertyValueDTO;

@Mapper(componentModel = "spring")
public abstract class PropValueConvertor {
    @Autowired
    protected MultiLangConverter multiLangConverter;
    public abstract List<PropValueVO> toPropValueVOList(List<PropertyValueDTO> list);

    @Mapping(target = "value", expression = "java(multiLangConverter.mText_to_str(vo.getValue()))")
    public abstract PropValueVO toPropValueVO(PropertyValueDTO vo);
}
