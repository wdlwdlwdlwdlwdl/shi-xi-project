package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderInventoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.UnlockInventoryTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderInventoryServiceImpl implements OrderInventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private UnlockInventoryTask unlockInventoryTask;

    @Override

    public boolean lockInventory(List<MainOrder> orders) {
        List<ScheduleTask> taskList = getUnlockTasks(orders);
        List<InventoryReduceParam> lockList = getReduceList(orders);

        // 先创建解锁任务，在锁库存
        List<ScheduleTaskId> ids = orderTaskService.createScheduledTask(taskList);
        boolean success = inventoryRepository.lockInventory(lockList);
        if (!success) {
            orderTaskService.deleteScheduledTask(ids);
        }
        return success;
    }

    @Override
    public void unlockInventory(List<MainOrder> orders) {
        List<InventoryReduceParam> list = getReduceList(orders);
        inventoryRepository.unlockInventory(list);
    }

    @Override
    public boolean reduceInventory(List<MainOrder> orders) {
        List<InventoryReduceParam> list = getReduceList(orders);
        return inventoryRepository.reduceInventory(list);
    }

    @Override
    public void rollbackInventoryBeforePay(List<MainOrder> orders) {
        List<InventoryRollbackParam> list = getRollbackList(orders);
        inventoryRepository.rollbackInventory(list);
    }

    private List<ScheduleTask> getUnlockTasks(List<MainOrder> orders) {
        List<ScheduleTask> list = new ArrayList<>();
        for (MainOrder main : orders) {
            ScheduleTask task = unlockInventoryTask.buildTask(new TaskParam(
                    main.getSeller().getSellerId(),
                    main.getPrimaryOrderId(),
                    BizCodeEntity.getOrderBizCode(main)));
            list.add(task);
        }
        return list;
    }

    private List<InventoryReduceParam> getReduceList(List<MainOrder> orders) {
        return orders.stream()
                .flatMap(main -> main.getSubOrders().stream())
                .map(sub -> {
                    InventoryReduceParam p = new InventoryReduceParam();
                    p.setOrderId(sub.getOrderId());
                    p.setSkuId(sub.getItemSku().getItemSkuId());
                    p.setSkuQty(sub.getOrderQty());
                    return p;
                }).collect(Collectors.toList());
    }

    private List<InventoryRollbackParam> getRollbackList(List<MainOrder> orders) {
        return orders.stream()
                .flatMap(main -> main.getSubOrders().stream())
                .map(sub -> {
                    InventoryRollbackParam p = new InventoryRollbackParam();
                    p.setOrderId(sub.getOrderId());
                    p.setSkuId(sub.getItemSku().getItemSkuId());
                    p.setSkuQty(sub.getOrderQty());
                    p.setReversalId(null);
                    return p;
                }).collect(Collectors.toList());
    }
}
