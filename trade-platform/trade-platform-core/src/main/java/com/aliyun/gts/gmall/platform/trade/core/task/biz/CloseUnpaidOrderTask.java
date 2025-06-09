package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 未支付订单超时关闭任务
 */
@Slf4j
@Component
public class CloseUnpaidOrderTask extends AbstractOrderStatusChangeTask {

    @Autowired
    private OrderConfigService orderConfigService;

    @Override
    protected ScheduleTask doBuildTask(TaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * cfg.getAutoCloseOrderTimeInSec());

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
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.CANCELLED);
        orderStatus.setPrimaryOrderId(task.getPrimaryOrderId());
        orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_PAYMENT);
        return doExecute(
            task,
            orderStatus,
            workflowProperties.getOrderSystemClose(),
            OrderChangeOperateEnum.CUST_CANCEL
        );
    }
}
