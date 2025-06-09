package com.aliyun.gts.gmall.center.trade.core.task.biz;

import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSysProcessService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 尾款支付超时，打款给卖家
 */
@Slf4j
@Component
public class DepositTailFeeTimeoutTask extends AbstractScheduledTask<ScheduleTimeTaskParam> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderSysProcessService orderSysProcessService;

    static {
        TaskRegister.addTaskClass(DepositTailFeeTimeoutTask.class);
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
        MainOrder mainOrder = orderQueryAbility.getMainOrder(task.getPrimaryOrderId());
        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
        if (!StepOrderStatusEnum.STEP_WAIT_PAY.getCode().equals(stepOrder.getStatus())) {
            return ExecuteResult.success("no need");
        }

        orderSysProcessService.systemConfirm(mainOrder);
        return ExecuteResult.success("ok");
    }
}
