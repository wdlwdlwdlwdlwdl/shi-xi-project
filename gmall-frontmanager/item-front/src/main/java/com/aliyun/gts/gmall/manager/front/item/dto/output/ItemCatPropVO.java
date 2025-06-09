package com.aliyun.gts.gmall.manager.front.item.dto.output;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 
* @Title: ItemCatPropVO.java 
* @Description: TODO(用一句话描述该文件做什么) 
* @author zhao.qi
* @date 2024年10月28日 14:40:31 
* @version V1.0
 */
@Getter
@Setter
public class ItemCatPropVO {
    private Long propId;
    private String propName;
    private Long catId;
    private List<String> propValue;
}