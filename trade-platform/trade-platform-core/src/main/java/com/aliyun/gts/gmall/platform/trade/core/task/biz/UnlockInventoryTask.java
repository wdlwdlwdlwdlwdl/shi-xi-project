package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 预占库存超时释放任务
 */
@Component
@Slf4j
public class UnlockInventoryTask extends AbstractScheduledTask {
    @Autowired
    private OrderInventoryAbility orderInventoryAbility;
    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Override
    protected ScheduleTask doBuildTask(TaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * cfg.getAutoUnlockInventoryTimeInSec());

        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(scheduleTime);
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        // 如果有自定义参数在这里添加
        // t.setParams(xxx);
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        Long primaryOrderId = task.getPrimaryOrderId();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        if (mainOrder == null) {
            return ExecuteResult.success("no order");
        }
        if (OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()) != OrderStatusEnum.ORDER_WAIT_PAY) {
            return ExecuteResult.success("no need");
        }
        orderInventoryAbility.unlockInventory(List.of(mainOrder));
        return ExecuteResult.success("ok");
    }
}
