package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CouponInstanceDetailVO {

    /**
     * 优惠券领取的code
     */
    private String couponCode;
    /**
     * 优惠券的状态
     * AssetsStatus
     */
    private Integer status;
    /**
     *
     */
    private Long campaignId;
    /**
     * 预热期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preStartTime;
    /**
     * 预热期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date preEndTime;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 活动名称
     */
    private String  name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 展示文案
     */
    private JSONObject display;

    /**
     * 门槛
     */
    private Long thresholdAmount;

    /**
     * 优惠目标类型（展示类属性）
     */
    private Integer promotionTargetType;

    /**
     * 限制商品ids
     */
    private List<Long> limitItemIds;

    /**
     * 限制商品ids
     */
    private List<Long> limitItemGroupIds;

    /**
     * 限制卖家
     */
    private String limitSeller;

    /**
     * 限制卖家ids
     */
    private List<Long> limitSellerIds;

}
