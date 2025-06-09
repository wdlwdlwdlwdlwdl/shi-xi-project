package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author gshine
 * @since 2/24/21 11:32 AM
 */
@Getter
@Setter
@ToString
public class CategoryVO {

    private Long id;
    private String name;
    private Long parentId;
    private Boolean leafYn;
    private Integer status;
    private String operator;
    private String gmtCreate;
    private String gmtModified;

}
