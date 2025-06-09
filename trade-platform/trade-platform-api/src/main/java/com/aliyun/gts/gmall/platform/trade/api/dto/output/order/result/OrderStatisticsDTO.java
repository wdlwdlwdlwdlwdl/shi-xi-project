package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @Title: OrderStatisticsDTO.java
 * @Description:
 * @author zhao.qi
 * @date 2024年8月12日 14:01:32
 * @version V1.0
 */
@Getter
@Setter
public class OrderStatisticsDTO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;
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
    
    public static OrderStatisticsDTO getDefaultInstance(Long sellerId) {
        OrderStatisticsDTO instance = new OrderStatisticsDTO();
        instance.setSellerId(sellerId);
        instance.setTotalCount(0L);
        instance.setTotalCountLast30Days(0L);
        instance.setTotalCompleteCountLast30Days(0L);
        instance.setCancelCount(0L);
        instance.setCancelCountLast30Days(0L);
        return instance;
    }
}
