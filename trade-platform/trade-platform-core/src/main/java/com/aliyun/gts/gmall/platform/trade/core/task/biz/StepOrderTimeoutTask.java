package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.BaseStepOperate;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSysProcessService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.StepTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class StepOrderTimeoutTask extends AbstractScheduledTask<StepTaskParam> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderSysProcessService orderSysProcessService;
    @Autowired
    private StepOrderProcessAbility stepOrderProcessAbility;


    @Override
    protected ScheduleTask doBuildTask(StepTaskParam param) {
        Long timeoutSec = param.getOp().getTimeoutInSec();
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * timeoutSec);

        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(scheduleTime);
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        t.putParam("stepNo", param.getStepOrder().getStepNo());
        t.putParam("stepStatus", param.getStepOrder().getStatus());
        t.putParam("action", param.getOp().getTimeoutAction());
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) throws Exception {
        Integer stepNo = task.getParam("stepNo");
        Integer stepStatus = task.getParam("stepStatus");
        String action = task.getParam("action");

        MainOrder mainOrder = orderQueryAbility.getMainOrder(task.getPrimaryOrderId());
        if (!OrderStatusEnum.STEP_ORDER_DOING.getCode().equals(mainOrder.getPrimaryOrderStatus()) && !OrderStatusEnum.PARTIALLY_PAID.getCode().equals(mainOrder.getPrimaryOrderStatus())) {
            return ExecuteResult.success("no need");
        }

        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
        if (stepOrder.getStepNo().intValue() != stepNo
                || stepOrder.getStatus().intValue() != stepStatus) {
            return ExecuteResult.success("no need");
        }

        if (BaseStepOperate.TIMEOUT_ACTION_NEXT.equals(action)) {
            // 进入下一步
            autoNextStep(mainOrder);
        } else if (BaseStepOperate.TIMEOUT_ACTION_SYS_PAID.equals(action)) {
            // 关闭交易 & 打款给卖家
            autoPaid(mainOrder);
        } else if (BaseStepOperate.TIMEOUT_ACTION_SYS_REFUND.equals(action)) {
            // 关闭交易 & 自动退款
            autoRefund(mainOrder);
        } else {
            return ExecuteResult.fail("not support " + action);
        }
        return ExecuteResult.success("ok");
    }

    private void autoNextStep(MainOrder mainOrder) {
        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
        StepOrderStatusEnum stepStatus = StepOrderStatusEnum.codeOf(stepOrder.getStatus());
        if (stepStatus == StepOrderStatusEnum.STEP_WAIT_CONFIRM) {
            StepOrderHandleRpcReq req = new StepOrderHandleRpcReq();
            req.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            req.setStepNo(stepOrder.getStepNo());
            stepOrderProcessAbility.onCustomerConfirm(mainOrder, req);
        }
    }

    private void autoPaid(MainOrder mainOrder) {
        orderSysProcessService.systemConfirm(mainOrder);
    }

    private void autoRefund(MainOrder mainOrder) {
        SystemRefund sysRefund = new SystemRefund();
        sysRefund.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        sysRefund.setMainOrder(mainOrder);
        orderSysProcessService.systemRefund(sysRefund);
    }
}
