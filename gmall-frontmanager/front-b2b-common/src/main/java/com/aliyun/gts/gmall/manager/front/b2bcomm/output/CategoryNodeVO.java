package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author gshine
 * @since 3/1/21 4:46 PM
 */
@Getter
@Setter
@ToString
public class CategoryNodeVO {

    private Long id;
    private String name;
    private List<CategoryNodeVO> children;
}
