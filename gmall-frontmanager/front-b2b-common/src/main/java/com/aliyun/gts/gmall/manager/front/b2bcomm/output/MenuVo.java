package com.aliyun.gts.gmall.manager.front.b2bcomm.output;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/22 15:11
 */
@Data
public class MenuVo implements Serializable {

    private Long id;
    private Long fatherId;
    /**
     * 父节点ID
     */
    private String code;
    private String description;
    private String url;
    private String icon;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 子节点
     */
    private List<MenuVo> children;

    public void addChild(MenuVo child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}
