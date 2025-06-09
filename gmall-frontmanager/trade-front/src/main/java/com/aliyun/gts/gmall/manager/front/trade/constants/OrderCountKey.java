package com.aliyun.gts.gmall.manager.front.trade.constants;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;

/**
 * 订单统计的key
 *
 * @author 俊贤
 * @date 2021/03/09
 */
public interface OrderCountKey {
    /**
     * 等待评价
     */
    Integer waitEvaluateKey  = -30;
    /**
     * 代付款
     */
    Integer waitPayKey       = OrderStatusEnum.ORDER_WAIT_PAY.getCode();
    /**
     * 等待收货
     */
    Integer waitReceptKey    = OrderStatusEnum.ORDER_SENDED.getCode();
    /**
     * 售后中
     */
    Integer reversalDoingKey = OrderStatusEnum.REVERSAL_DOING.getCode();
}