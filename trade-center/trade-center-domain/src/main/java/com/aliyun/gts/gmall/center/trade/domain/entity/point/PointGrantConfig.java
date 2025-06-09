package com.aliyun.gts.gmall.center.trade.domain.entity.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointGrantConfig {

    /**
     * 下单是否赠送积分
     */
    private Boolean tradeGrantPoint;

    /**
     * 下单每满1元, 赠送的原子积分数
     */
    private Long grantPointOneYuan;

    /**
     * 积分失效类型 1是长期; 2是年; 3是月;
     */
    private Integer invalidType;
    /**
     * 积分失效年
     */
    private Integer invalidYear;
    /**
     * 积分失效月分
     */
    private Integer invalidMonth;

    /**
     * 赠送积分保留日期，消费者未在规定时间内退款，则下发积分；
     */
    private Integer grantPointReserveDay;

    public boolean isTradeGrantPoint() {
        return tradeGrantPoint != null && tradeGrantPoint.booleanValue()
                && grantPointOneYuan != null && grantPointOneYuan.longValue() > 0;
    }
}
