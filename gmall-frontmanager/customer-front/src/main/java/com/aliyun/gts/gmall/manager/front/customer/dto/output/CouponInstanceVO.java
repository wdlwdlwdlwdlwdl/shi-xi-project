package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author 俊贤
 * @date 2021/03/12
 */
@Data
public class CouponInstanceVO {
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
    private Date  startTime;
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 备注
     */
    private String remark;
    /**
     * 展示文案
     */
    private JSONObject display;

    /**
     * 领取标记
     */
    private Boolean received = false;

    /**
     * 领取消息
     */
    private String receiveMsg ;

    /**
     * 优惠券活动状态
     */
    private Integer campaignStatus;


    /**
     * 当前卖家id
     */
    private Long sellerId;


    /**
     * 当前卖家id
     */
    private String sellerName;

    /**
     *卖家ka标识
     */
    private Integer sellerKaTag;


}