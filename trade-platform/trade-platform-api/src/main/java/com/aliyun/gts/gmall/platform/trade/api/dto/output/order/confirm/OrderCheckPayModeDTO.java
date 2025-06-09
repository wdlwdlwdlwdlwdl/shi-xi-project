package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单确认页面
 * 所有商品支付方式金额交集对象
 * @anthor shifeng
 * 2024-12-11 20:38:30
 */
@Data
public class OrderCheckPayModeDTO implements Serializable {
    // 支付方式
    private String payMode;
    // 分期数
    private Integer type;
    // 总金额
    private Long totalAmount;
    // 月金额
    private Long monthAmount;
    // 营销总金额
    private Long totalPromotionAmount;
    // 月金额 -- 营销后
    private Long monthPromotiomAmount;
    
}