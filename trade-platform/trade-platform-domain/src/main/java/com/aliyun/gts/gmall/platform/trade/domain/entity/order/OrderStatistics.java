package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: OrderStatistics.java
 * @Description: 根据商家统计订单
 * @author zhao.qi
 * @date 2024年8月12日 11:33:20
 * @version V1.0
 */
@Getter
@Setter
public class OrderStatistics {
    /** 商家id */
    private Long sellerId;
    /** 订单总数 */
    private Long totalCount;
    /** 过去30天订单总数 */
    private Long totalCountLast30Days;

    /** 过去30天完成订单总数 */
    private Long totalCompleteCountLast30Days;
    /** 订单总取消数 */
    private Long cancelCount;
    /** 过去30天订单总取消数 */
    private Long cancelCountLast30Days;
}
