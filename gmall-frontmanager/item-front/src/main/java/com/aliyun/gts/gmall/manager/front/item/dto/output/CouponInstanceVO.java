package com.aliyun.gts.gmall.manager.front.item.dto.output;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 俊贤
 * @date 2021/03/12
 */
@Data
public class CouponInstanceVO implements Serializable {
    /**
     * 优惠券领取的code
     */
    private String     couponCode;
    /**
     * 优惠券的状态
     * AssetsStatus
     */
    private Integer    status;
    /**
     *
     */
    private Long       campaignId;
    /**
     * 预热期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       preStartTime;
    /**
     * 预热期结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       preEndTime;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       startTime;
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       endTime;
    /**
     * 活动名称
     */
    private String     name;
    /**
     * 备注
     */
    private String     remark;
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

    /**
     * 门槛
     */
    private Long thresholdAmount;

    /**
     * 限制商品ids
     */
    private List<Long> limitItemIds;

    /**
     * 券是否领完
     */
    private Boolean couponPickOver = Boolean.FALSE;

    /**
     * 可以领取
     */
    private Boolean canPick = Boolean.TRUE;

}