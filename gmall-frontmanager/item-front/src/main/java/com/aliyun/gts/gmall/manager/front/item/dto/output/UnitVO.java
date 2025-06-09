package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.Date;
import java.util.Set;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: UnitVO.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author zhao.qi
 * @date 2025年1月13日 11:45:39
 * @version V1.0
 */
@Getter
@Setter
public class UnitVO {
    private Long id;
    private String unitName;
    private Set<LangText> unitNameI18n;
    private String abbreviation;
    private Set<LangText> abbreviationI18n;
    private String attributeRelation;
    private Date gmtCreate;
    private Date gmtModified;
    private String operator;
    private String creator;
}
