package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.alibaba.fastjson.JSONArray;
import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.FileDO;
import com.aliyun.gts.gmall.manager.front.b2bcomm.output.CategoryVO;
import lombok.Data;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/14 12:52
 */
@Data
public class SourcingMaterialVo extends BaseDTO {

    private Long bizId;
    private String name;
    private String code;
    private Long materialId;
    private Long categoryId;
    private Integer num;
    private String unit;
    private String brandName;
    private String model;
    private Integer status;
    private String description;
    private String feature;
    private FileDO attach;
    private Long operatorId;

    /**
     * 类目路径
     */
    private List<CategoryVO> categoryList;
    /**
     * 属性
     */
    private JSONArray propertyValue;
}
