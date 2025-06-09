package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/1 14:58
 */
@Data
public class StepVo implements Serializable {
    private Integer current = 0;
    private String currentName = "";

    private List<Step> list;

    public void addStep(String name,String desc) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(new Step(name,desc));
    }
    public void addStep(Step step) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(step);
    }

    @Data
    private static class Step implements Serializable {
        private Integer status;
        private String name;
        //描述
        private String description;
        public Step(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}
