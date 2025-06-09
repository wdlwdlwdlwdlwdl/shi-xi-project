package com.aliyun.gts.gmall.center.trade.core.task.biz;

import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSysProcessService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 交期到期未发货，自动退款
 */
@Slf4j
@Component
public class DepositLastSendTimeTask extends AbstractScheduledTask<ScheduleTimeTaskParam> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderSysProcessService orderSysProcessService;

    // 开关, 是否自动关闭交易并退款
    @Value("${trade.deposit.lastSendTimeoutAutoClose:false}")
    private boolean lastSendTimeoutAutoClose;

    static {
        TaskRegister.addTaskClass(DepositLastSendTimeTask.class);
    }

    @Override
    protected ScheduleTask doBuildTask(ScheduleTimeTaskParam param) {
        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(param.getScheduleTime());
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) throws Exception {
        if (!lastSendTimeoutAutoClose) {
            return ExecuteResult.success("switch off");
        }
        MainOrder mainOrder = orderQueryAbility.getMainOrder(task.getPrimaryOrderId());
        if (!OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().equals(mainOrder.getPrimaryOrderStatus())) {
            return ExecuteResult.success("no need");
        }

        // 退款
        SystemRefund sysRefund = new SystemRefund();
        sysRefund.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        sysRefund.setMainOrder(mainOrder);
        orderSysProcessService.systemRefund(sysRefund);
        return ExecuteResult.success("ok");
    }
}
