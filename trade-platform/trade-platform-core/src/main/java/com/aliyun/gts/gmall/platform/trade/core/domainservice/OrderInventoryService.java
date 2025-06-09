package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

import java.util.List;

public interface OrderInventoryService {

    /**
     * 预占库存, 一次传多个主订单时可保证事务, 同时会生成超时解锁任务
     */
    boolean lockInventory(List<MainOrder> orders);

    /**
     * 释放预占库存
     */
    void unlockInventory(List<MainOrder> orders);

    /**
     * 减库存, 要求先预占
     */
    boolean reduceInventory(List<MainOrder> orders);

    /**
     * 回滚库存（非售后逆向的）
     */
    void rollbackInventoryBeforePay(List<MainOrder> orders);
}
