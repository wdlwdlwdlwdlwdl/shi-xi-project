package com.aliyun.gts.gmall.platform.trade.domain.entity.item;

import lombok.Data;

import java.util.Date;

/**
 * 商品物流信息对象
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-23 14:14:49
 */
@Data
public class ItemDelivery {

    // 物流方式
    private Integer deliveryType;

    // 物流方式
    private String deliveryTypeName;

    // 配送时间
    private Integer deliverHours;

    // 配送时间
    private Date deliverTime;

}
