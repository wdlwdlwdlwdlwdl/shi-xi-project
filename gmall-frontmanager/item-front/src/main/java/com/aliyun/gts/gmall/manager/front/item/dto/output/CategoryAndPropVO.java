package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAndPropVO {
    List<CategoryTreeVO> categoryTreeList;

    List<ItemCatPropValueVO> catPropValueList;
}
