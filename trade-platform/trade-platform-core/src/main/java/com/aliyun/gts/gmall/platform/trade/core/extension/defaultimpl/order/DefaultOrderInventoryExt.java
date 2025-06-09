package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.UnlockInventoryTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/20 10:26
 */
@Slf4j
@Component
public class DefaultOrderInventoryExt implements OrderInventoryExt {

    @Autowired
    protected InventoryRepository inventoryRepository;
    @Autowired
    protected OrderTaskService orderTaskService;
    @Autowired
    protected UnlockInventoryTask unlockInventoryTask;

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
        List<SubOrder> subOrders = new ArrayList<>();
        for (MainOrder order : orders) {
            subOrders.addAll(order.getSubOrders());
        }
        List<InventoryRollbackParam> list = getRollbackList(subOrders);
        inventoryRepository.rollbackInventory(list);
    }

    @Override
    public void rollbackInventoryAfterRefund(MainReversal mainReversal) {
        List<InventoryRollbackParam> list = getRollbackList(mainReversal);
        inventoryRepository.rollbackInventory(list);
    }

    /**
     * 关闭订单回滚库存
     *
     * @param mainReversal
     * @return
     */
    protected List<InventoryRollbackParam> getRollbackList(MainReversal mainReversal) {
        List<InventoryRollbackParam> inventoryRollbackParams = new ArrayList<>();
        mainReversal.getSubReversals().stream().forEach(subReversal -> {
            InventoryRollbackParam inventoryRollbackParam = new InventoryRollbackParam();
            inventoryRollbackParam.setOrderId(subReversal.getSubOrder().getOrderId());
            inventoryRollbackParam.setReversalId(subReversal.getReversalId());
            ItemSkuId itemSkuId = new ItemSkuId();
            itemSkuId.setItemId(subReversal.getSubOrder().getItemSku().getItemId());
            itemSkuId.setSkuId(subReversal.getSubOrder().getItemSku().getSkuId());
            inventoryRollbackParam.setSkuId(itemSkuId);
            inventoryRollbackParam.setSkuQty(subReversal.getCancelQty());
            inventoryRollbackParams.add(inventoryRollbackParam);
        });
        return inventoryRollbackParams;
    }

    private List<ScheduleTask> getUnlockTasks(List<MainOrder> orders) {
        List<ScheduleTask> list = new ArrayList<>();
        for (MainOrder main : orders) {

            ScheduleTask task = unlockInventoryTask.buildTask(new TaskParam(
                    main.getSeller().getSellerId(),
                    main.getPrimaryOrderId(),
                    BizCodeEntity.getOrderBizCode(main)));
            task.setMainOrder(main);
            list.add(task);

        }
        return list;
    }

    /**
     * 库存扣减
     *
     * @param orders
     * @return
     */
    protected List<InventoryReduceParam> getReduceList(List<MainOrder> orders) {
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

    /**
     * 关闭订单回滚库存;
     *
     * @param orders
     * @return
     */
    protected List<InventoryRollbackParam> getRollbackList(List<SubOrder> orders) {
        return orders.stream()
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
