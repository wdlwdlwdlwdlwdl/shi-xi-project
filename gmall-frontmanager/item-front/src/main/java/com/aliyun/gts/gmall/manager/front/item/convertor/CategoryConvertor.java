package com.aliyun.gts.gmall.manager.front.item.convertor;

import com.aliyun.gts.gmall.manager.front.item.dto.output.CategoryTreeVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryTreeDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryConvertor {
    // CategoryTreeDTO <---> CategoryTreeDTO
    CategoryTreeVO toCategoryTreeVo(CategoryTreeDTO taget);

    List<CategoryTreeVO> toCategoryTreeVos(List<CategoryTreeDTO> list);
}
