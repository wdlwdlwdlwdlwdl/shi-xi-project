package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.TimeoutSettingService;
import com.aliyun.gts.gmall.platform.trade.core.task.param.CancelTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 说明： 不同订单状态自动取消任务
 *
 * @author yangl
 * @version 1.0
 * @date 2024/10/16 17:02
 */
@Slf4j
@Component
public class AutoCancelOrderTask extends AbstractOrderStatusChangeTask<CancelTaskParam> {

    @Autowired
    private TimeoutSettingService timeoutSettingService;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderStatusChangeFlowComponent orderStatusChangeFlowComponent;

    private static final String KEY_BIZ_CODE = "bizCodes";

    @Override
    public ScheduleTask doBuildTask(CancelTaskParam param) {
        List<BizCodeEntity> bizCodes = param.getBizCodes();
        Date scheduleTime ;
        if(param.getTimeType() == 0){
            //分
            scheduleTime = new Date(System.currentTimeMillis() + 60*1000L * Long.parseLong(param.getTimeRule()));
        }else if(param.getTimeType() == 1){
            //时
            scheduleTime = new Date(System.currentTimeMillis() + 60*60*1000L * Long.parseLong(param.getTimeRule()));
        }else{
            //天
            scheduleTime = new Date(System.currentTimeMillis() + 60*60*24*1000L * Long.parseLong(param.getTimeRule()));
        }
        ScheduleTask task = new ScheduleTask();
        task.setScheduleTime(scheduleTime);
        task.setType(getTaskType());
        task.setPrimaryOrderId(param.getPrimaryOrderId());
        task.putParam("orderStatus", param.getOrderStatus());
        task.putParam("payType", param.getPayType());
        task.putParam("primaryOrderId", param.getPrimaryOrderId());
        task.putParam("timeType", param.getTimeType());
        task.putParam("timeRule", param.getTimeRule());
        return task;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) throws Exception {
        Map<String, Object> params = task.getParams();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
        orderStatus.setPrimaryOrderId(task.getPrimaryOrderId());
        orderStatus.setCheckStatus(OrderStatusEnum.codeOf(Integer.valueOf(params.get("orderStatus").toString())));
        Long primaryOrderId = task.getPrimaryOrderId();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        if (mainOrder == null) {
            return ExecuteResult.success("no order");
        }
        if (!Objects.equals(mainOrder.getPrimaryOrderStatus(), orderStatus.getCheckStatus().getCode())) {
            return ExecuteResult.success("no need");
        }
        FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
            workflowProperties.getOrderSystemCancel(),
            BizCodeEntity.buildWithDefaultBizCode(mainOrder),
            orderStatus,
            OrderChangeOperateEnum.CANCELLED
        );
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(result.getFail()));
        }
        return ExecuteResult.success("ok");
    }
}
