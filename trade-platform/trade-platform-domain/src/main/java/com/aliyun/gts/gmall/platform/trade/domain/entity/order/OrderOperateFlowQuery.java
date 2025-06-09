package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import lombok.Data;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/26 19:41
 */
@Data
public class OrderOperateFlowQuery {

    /**
     * 主订单ID
     */
    private Long primaryOrderId;

    /**
     * 变更前订单状态
     */
    private Integer fromOrderStatus;

    /**
     * 变更后订单状态
     */
    private Integer toOrderStatus;

}
