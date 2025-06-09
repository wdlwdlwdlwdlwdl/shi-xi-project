package com.aliyun.gts.gmall.manager.front.customer.dto.output.customer;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品营销信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品营销信息")
public class CustomerPromotionVO extends AbstractOutputInfo {
    @ApiModelProperty("优惠券ID")
    private String     couponCode;
    @ApiModelProperty("是否优惠券")
    private Boolean    isCoupon;
    @ApiModelProperty("优惠券展示信息")
    private JSONObject display;
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       startTime;
    @ApiModelProperty("截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date       endTime;
    @ApiModelProperty("活动名称")
    private String     name;
    @ApiModelProperty("活动ID")
    private Long       campaignId;
    @ApiModelProperty("活动是否开始,非预热")
    private Boolean    promStart = false;
    @ApiModelProperty("活动是否可参加/券是否可领取")
    private Boolean    canApply;
    @ApiModelProperty("活动是否已参加/券是否已领取")
    private Boolean    received;

    @ApiModelProperty("卖家id")
    private Long sellerId;
    @ApiModelProperty("卖家名称")
    private String sellerName;
    @ApiModelProperty("卖家Ka标识")
    private Integer sellerKaTag;
    @ApiModelProperty("门槛")
    private Long thresholdAmount;
    @ApiModelProperty("限制商品ids")
    private List<Long> limitItemIds;
}
