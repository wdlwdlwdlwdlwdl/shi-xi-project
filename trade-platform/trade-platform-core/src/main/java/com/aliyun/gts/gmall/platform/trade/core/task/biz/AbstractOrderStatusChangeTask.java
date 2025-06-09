package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.collections.Lists;

import java.util.List;

@Slf4j
public abstract class AbstractOrderStatusChangeTask<P extends TaskParam> extends AbstractScheduledTask<P> {
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderStatusChangeFlowComponent orderStatusChangeFlowComponent;
    @Autowired
    protected WorkflowProperties workflowProperties;

    protected ExecuteResult doExecute(
        ScheduleTask task,
        OrderStatus orderStatus,
        String flowName,
        OrderChangeOperate op) {

        Long primaryOrderId = task.getPrimaryOrderId();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        if (mainOrder == null) {
            return ExecuteResult.success("no order");
        }
        if (!mainOrder.getPrimaryOrderStatus().equals(orderStatus.getCheckStatus().getCode())) {
            return ExecuteResult.success("no need");
        }
        List<String> bizCodes = mainOrder.getBizCodes();
        List<String> failList = Lists.newArrayList();
        bizCodes.stream().forEach(bizCode -> {
            BizCodeEntity bizCodeEntity = BizCodeEntity.buildByCode(bizCode);
            FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                flowName,
                bizCodeEntity,
                orderStatus,
                op
            );
            if (!result.isSuccess()) {
                failList.add(result.getFail().getMessage());
            }
        });
        if (failList.size() > 0) {
            return ExecuteResult.fail(JSON.toJSONString(failList));
        } else {
            return ExecuteResult.success("ok");
        }
    }
}
