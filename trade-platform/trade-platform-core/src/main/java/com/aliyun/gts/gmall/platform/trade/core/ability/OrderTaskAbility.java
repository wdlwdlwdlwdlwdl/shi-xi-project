package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface OrderTaskAbility {

    /**
     * 创建取消任务
     */
    void orderTask(MainOrder order,Integer orderStatus);

    /**
     * 创建售后取消任务
     */
    void reversalTask(MainReversal reversal, Integer orderStatus);
}
