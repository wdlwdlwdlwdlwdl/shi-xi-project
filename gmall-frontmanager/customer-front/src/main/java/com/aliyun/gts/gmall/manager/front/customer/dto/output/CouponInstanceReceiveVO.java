package com.aliyun.gts.gmall.manager.front.customer.dto.output;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t
 * @date 2022/10/17
 */
@Data
public class CouponInstanceReceiveVO {

    /**
     * 领取标记
     */
    private Boolean received = true;

    /**
     * 领取消息
     */
    private List<CouponInstanceVO> couponInstanceVOList = new ArrayList<>();

}