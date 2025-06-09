package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryRollbackParam;

import java.util.List;

public interface InventoryRepository {

    /**
     * 预占库存, 全部成功或全部失败
     * 库存不足/商品失效/SKU失效时返回false, 其他失败情况抛异常
     */
    boolean lockInventory(List<InventoryReduceParam> list);

    /**
     * 释放预占库存
     */
    void unlockInventory(List<InventoryReduceParam> list);

    /**
     * 扣减预占的库存，库存不足/商品失效/SKU失效时返回false, 其他失败情况抛异常
     */
    boolean reduceInventory(List<InventoryReduceParam> list);

    /**
     * 回滚库存（增加库存）
     */
    void rollbackInventory(List<InventoryRollbackParam> list);
}
