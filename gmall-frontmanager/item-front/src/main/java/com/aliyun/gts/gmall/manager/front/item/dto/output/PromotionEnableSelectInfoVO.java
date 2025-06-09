package com.aliyun.gts.gmall.manager.front.item.dto.output;

import lombok.Data;

import java.util.List;

@Data
public class PromotionEnableSelectInfoVO {

    /**
     * 优惠券
     */
    private List<CouponInstanceVO> couponInstanceList;

    /**
     * 折扣
     */
    List<PromotionDetailVO> promotionDetailList;



}
