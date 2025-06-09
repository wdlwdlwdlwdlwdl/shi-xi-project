package com.aliyun.gts.gmall.platform.trade.core.task.biz;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsOuterStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsOuterStatusService.ReceiveStatus;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.param.SysConfirmTaskParam;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统自动确认收货
 */
@Slf4j
@Component
public class SystemConfirmOrderTask extends AbstractOrderStatusChangeTask<SysConfirmTaskParam> {

    @Autowired
    private OrderConfigService orderConfigService;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private LogisticsOuterStatusService logisticsOuterStatusService;
    @Autowired
    private OrderTaskService orderTaskService;

    @Override
    protected ScheduleTask doBuildTask(SysConfirmTaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());

        Integer timeInSec = cfg.getAutoConfirmReceiveTimeInSec();
        boolean force = false;
        if (param.isLogisticsDelay()) {  // 延后确认收货
            timeInSec = cfg.getDeliveryDelayConfirmReceiveTimeInSec();
            force = true;
        }

        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * timeInSec);
        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(scheduleTime);
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());

        Map<String, Object> ext = new HashMap<>();
        ext.put("force", force);
        t.setParams(ext);
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(task.getPrimaryOrderId());
        if (OrderStatusEnum.REVERSAL_DOING.getCode().equals(mainOrder.getPrimaryOrderStatus())) {
            return ExecuteResult.later("later:"+I18NMessageUtils.getMessage("aftersale.process"));  //# 售后进行中"
        }

        // 非强制确认, 物流未签收, 延后确认收货一次
        Boolean force = task.getParam("force");
        if (!Boolean.TRUE.equals(force) && needDelay(mainOrder)) {
            createDelayTask(mainOrder);
            return ExecuteResult.success("delay confirm");
        }

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.SYSTEM_CONFIRM);
        orderStatus.setPrimaryOrderId(task.getPrimaryOrderId());
        orderStatus.setCheckStatus(OrderStatusEnum.ORDER_SENDED);
        return doExecute(task, orderStatus,
                workflowProperties.getOrderConfirmReceive(),
                OrderChangeOperateEnum.SYS_CONFIRM_RECEIVE);
    }

    protected boolean needDelay(MainOrder mainOrder) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(mainOrder.getSeller().getSellerId());

        // 未配置延长时长的, 不延长
        if (NumUtils.getNullZero(cfg.getDeliveryDelayConfirmReceiveTimeInSec()) <= 0) {
            return false;
        }

        ReceiveStatus status = logisticsOuterStatusService.getOrderReceiveStatus(mainOrder.getPrimaryOrderId());
        return status == ReceiveStatus.NOT_RECEIVED;    // 未签收状态 需要延长
    }

    protected void createDelayTask(MainOrder mainOrder) {
        SysConfirmTaskParam p = new SysConfirmTaskParam();
        p.setSellerId(mainOrder.getSeller().getSellerId());
        p.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        p.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
        p.setLogisticsDelay(true);
        ScheduleTask task = buildTask(p);
        orderTaskService.createScheduledTask(Lists.newArrayList(task));
    }
}
