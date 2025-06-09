package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.PropValueVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCatPropValueVO {
    public static final String TYPE_CUSTOM = "input";
    public static final String TYPE_SELECT = "select";

    /** 属性值id */
    private Long id;

    /** 所属类目id */
    private Long categoryId;

    /** 属性id */
    private Long propId;

    /** 属性名 */
    private String propName;

    /** 填写类型 **/
    private Integer fillType;

    /** 属性状态 */
    private Integer status;

    private List<PropValueVO> propValueList;
    
    private UnitVO unit;

    private Integer propSortNo;
}
