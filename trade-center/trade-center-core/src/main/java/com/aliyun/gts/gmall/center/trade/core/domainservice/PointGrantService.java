package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 下单赠送积分
 */
public interface PointGrantService {

    /**
     * 订单创建 -- 计算、记录赠送的积分数量
     */
    void grantOnOrderCreate(MainOrder mainOrder);

    /**
     * 订单完成(确认收货) -- 发放积分
     */
    void grantOnOrderSuccess(Long primaryOrderId);

    /**
     * 售后完成 -- 计算、退回积分
     */
    void rollbackOnReversalSuccess(Long primaryReversalId);

    /**
     * 退款之前处理赠送积分退回, 不扣成负数积分使用
     * @return true:继续退款, false:无需退款
     */
    boolean rollbackBeforeRefund(Long primaryReversalId);

}
