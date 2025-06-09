package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

import java.util.List;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/19 19:44
 */
public interface OrderInventoryExt extends IExtensionPoints {

    @AbilityExtension(
            code = "lockInventory",
            name = "库存冻结"
    )
    /**
     * 预占库存, 一次传多个主订单时可保证事务, 同时会生成超时解锁任务
     */
    boolean lockInventory(List<MainOrder> orders);

    @AbilityExtension(
            code = "unlockInventory",
            name = "库存释放"
    )
    /**
     * 释放预占库存
     */
    void unlockInventory(List<MainOrder> orders);

    @AbilityExtension(
            code = "reduceInventory",
            name = "库存扣减"
    )
    /**
     * 减库存, 要求先预占
     */
    boolean reduceInventory(List<MainOrder> orders);


    @AbilityExtension(
            code = "rollbackInventoryBeforePay",
            name = "回滚库存（非售后逆向的）"
    )
    void rollbackInventoryBeforePay(List<MainOrder> orders);

    @AbilityExtension(
            code = "rollbackInventoryAfterRefund",
            name = "库存回滚(逆向售后)"
    )
    void rollbackInventoryAfterRefund(MainReversal mainReversal);


}
