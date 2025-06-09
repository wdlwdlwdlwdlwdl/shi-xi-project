package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 
 * @Title: OrderStatisticsDTO.java
 * @Description:
 * @author yl
 * @version V1.0
 */
@Getter
@Setter
public class OrderSummaryStatisticsDTO extends AbstractOutputInfo {
    private static final long serialVersionUID = 1L;
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 订单数量
     */
    private Long orderNum;

    /**
     * 取消单数量
     */
    private Long cancelOrderNum;

    /**
     * 统计时间
     */
    private String statisticDate;

    /**
     * 取消比例
     */
    private String cancelRate;

    /**
     * 创建时间（统计时间）
     */
    private Date createTime;

    private int isSend;
    /**
     * 更新时间
     */
    private Date updateTime;
}
