package com.aliyun.gts.gmall.manager.front.sourcing.vo;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gcai.platform.sourcing.common.model.BaseDTO;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.FulfillPromise;
import com.aliyun.gts.gcai.platform.sourcing.common.model.inner.QuoteFeature;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/5/18 14:18
 */
@Data
public class QuoteVo extends BaseDTO {
    private Long sourcingId;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 采购商ID
     */
    public Long purchaserId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 税率
     */
    private Long taxRate;

    /**
     * 运费
     */
    private String freightCost;

    /**
     * 总价
     */
    private String totalPrice;

    /**
     * 报价信息
     */
    private JSONObject quoteFeature;

    private QuoteFeature feature;

    /**
     * 履约承诺
     */
    private FulfillPromise fulfillPromise;


    /**
     * 操作者
     */
    private Long operatorId;

    /**
     *
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
    /**
     * 报价有去
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date priceStartTime;
    /**
     * 价格有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date priceEndTime;

    /**
     * 报价详情
     */
    private List<QuoteDetailVo> details;
    /**
     * 询价活动信息
     */
    private SourcingVo sourcingInfo;
    /**
     * 报价排名
     */
    private Integer rank;
    /**
     * 总价分
     */
    private Long totalPriceFen;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;
}

