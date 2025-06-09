package com.aliyun.gts.gmall.center.trade.deposit.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.DepositConstants;
import com.aliyun.gts.gmall.center.trade.common.util.DateUtils;
import com.aliyun.gts.gmall.center.trade.core.constants.DepositErrorCode;
import com.aliyun.gts.gmall.center.trade.core.task.biz.DepositLastSendTimeTask;
import com.aliyun.gts.gmall.center.trade.core.task.biz.DepositTailFeeTimeoutTask;
import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.FormAction;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.aliyun.gts.gmall.center.trade.common.util.DateUtils.ON_DAY_MILLIS;
import static com.aliyun.gts.gmall.center.trade.common.util.DateUtils.toDayEnd;

/**
 * 定金尾款扩展
 */
@Slf4j
@Extension(points = {StepOrderProcessExt.class})
public class DepositStepOrderProcessExt extends DefaultStepOrderProcessExt {

    @Autowired
    private DepositTailFeeTimeoutTask depositTailFeeTimeoutTask;
    @Autowired
    private DepositLastSendTimeTask depositLastSendTimeTask;
    @Autowired
    private OrderTaskService orderTaskService;

    @Override
    protected void afterCustomerConfirmTx(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        super.afterCustomerConfirmTx(mainOrder, req);
        createTimeoutTask(mainOrder);
    }

    @Override
    protected void dispatchSellerHandleAction(FormAction action, MainOrder mainOrder,
                                        StepOrderHandleRpcReq req, List<OrderChangeNotify> messageList) {
        if ("PROCESS_LAST_SEND_TIME".equals(action.getName())) {
            processLastSendTime(action, mainOrder, req, messageList);
        } else {
            super.dispatchSellerHandleAction(action, mainOrder, req, messageList);
        }
    }

    // 处理交期, 创建超时任务
    private void processLastSendTime(FormAction action, MainOrder mainOrder,
                                     StepOrderHandleRpcReq req, List<OrderChangeNotify> messageList) {
        // 删除之前的交期任务
        orderTaskService.logicalDeleteByOrder(mainOrder.getPrimaryOrderId(), depositLastSendTimeTask.getTaskType());

        String targetTimeKey = action.getObject("targetTime");
        String targetTimeValue = mainOrder.orderAttr().getStepContextProps().get(targetTimeKey);
        Date targetTime = DateUtils.parseDateTime(targetTimeValue);
        if (targetTime.getTime() - System.currentTimeMillis() < DateUtils.ON_DAY_MILLIS) {
            throw new GmallException(DepositErrorCode.LAST_SEND_TIME_EARLIER);
        }

        ScheduleTimeTaskParam param = new ScheduleTimeTaskParam();
        param.setScheduleTime(targetTime);
        param.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        param.setSellerId(mainOrder.getSeller().getSellerId());
        param.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
        ScheduleTask scheduleTask = depositLastSendTimeTask.buildTask(param);
        orderTaskService.createScheduledTask(Arrays.asList(scheduleTask));
    }

    // 创建尾款支付超时任务
    private void createTimeoutTask(MainOrder mainOrder) {
        String tailDaysStr = mainOrder.orderAttr().stepContextProps().get(DepositConstants.CONTEXT_TAIL_DAYS);
        if (StringUtils.isBlank(tailDaysStr)) {
            return;
        }
        int tailDays = Integer.parseInt(tailDaysStr);
        long date = toDayEnd(System.currentTimeMillis() + ON_DAY_MILLIS * tailDays);

        ScheduleTimeTaskParam param = new ScheduleTimeTaskParam();
        param.setScheduleTime(new Date(date));
        param.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        param.setSellerId(mainOrder.getSeller().getSellerId());
        param.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
        ScheduleTask scheduleTask = depositTailFeeTimeoutTask.buildTask(param);
        orderTaskService.createScheduledTask(Arrays.asList(scheduleTask));
    }
}
