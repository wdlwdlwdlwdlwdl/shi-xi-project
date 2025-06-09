package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingMaterialDTO;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/18 14:48
 */
@Data
public class QuoteDetailVo extends SourcingMaterialDTO {
    /**
     * 报价单id
     */
    private Long quoteId;

    /**
     * 寻源物料id
     */
    private Long scMaterialId;

    /**
     * 税率
     */
    private Long taxRate;

    /**
     * 运费
     */
    private String freightCost;

    /**
     * 价格
     */
    private String price;

    /**
     * 展示数量
     */
    private Integer num;
    /**
     * 授标数量
     */
    private Integer awardNum;
    /**
     * 报价其他扩展信息
     */
    private QuoteDetailFeatureVO quoteFeature;
    /**
     *
     */
    private Integer status;
    /**
     * 供应商ID
     */
    private Long supplierId;
    /**
     * 是否命中
     */
    private Boolean selected = false;

    /**
     * 询价的物料信息
     */
    private SourcingMaterialVo material;
}
