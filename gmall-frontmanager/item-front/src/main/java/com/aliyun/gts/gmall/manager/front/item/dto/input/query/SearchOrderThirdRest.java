package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: Order.java
 * @Description: 排序字段
 * @author zhao.qi
 * @date 2024年9月25日 13:59:09
 * @version V1.0
 */
@Getter
@Setter
public class SearchOrderThirdRest {
    private String field;
    private String direction;
}
