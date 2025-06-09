package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 类目属性组
 */
@Getter
@Setter
public class ItemCatPropGroupVO {
    /** 属性组ID **/
    private Long id;
    /** 类目id **/
    private Long categoryId;
    /** 属性组名称 */
    private String groupName;

    /**
     * 属性组排序
     */
    private Integer groupSortNo;
    /** 属性值 */
    private List<ItemCatPropValueVO> categoryPropList;
}