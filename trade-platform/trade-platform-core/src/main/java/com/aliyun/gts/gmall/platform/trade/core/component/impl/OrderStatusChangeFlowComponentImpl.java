package com.aliyun.gts.gmall.platform.trade.core.component.impl;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.BizScope;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderStatusChangeFlowComponentImpl implements OrderStatusChangeFlowComponent {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private WorkflowEngine workflowEngine;

    /**
     * 修改订单状态接口
     * @param flowName
     * @param bizCodeEntity
     * @param orderStatus
     * @param op
     * @return
     */
    @Override
    public FlowNodeResult changeOrderStatus(
        String flowName, BizCodeEntity bizCodeEntity,
        OrderStatus orderStatus, OrderChangeOperate op) {

        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, orderStatus.getPrimaryOrderId())
        );
        log.info("changeOrderStatus order lock {}", orderStatus.getPrimaryOrderId());
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("changeOrderStatus order lock exception {} ", e.getMessage());
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            log.info("changeOrderStatus order lock fail {}", orderStatus.getPrimaryOrderId());
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("op", op);
            context.put("orderStatus", orderStatus);
            FlowNodeResult result = new BizScope<FlowNodeResult>(bizCodeEntity) {
                @Override
                protected FlowNodeResult execute() {
                    try {
                        return (FlowNodeResult) workflowEngine.invokeAndGetResult(flowName, context);
                    } catch (Exception e) {
                        log.error("workflowEngine.invokeAndGetResult occurred exceptions!", e);
                        throw (RuntimeException) e;
                    }
                }
            }.invoke();
            return result;
        } finally {
            orderLock.unLock();
        }
    }
}
