package com.aliyun.gts.gmall.center.trade.server.ext.toPay;

import com.aliyun.gts.gmall.framework.api.log.sdk.OpLogCommonUtil;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.AppIdConstants;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.BizTypeConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderMergePayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderAutoCancelConfigService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderRollCouponService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.server.facade.impl.OrderPayWriteFacadeImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Primary
@Component
@Slf4j
public class OrderPayWriteFacadeImplExt extends OrderPayWriteFacadeImpl {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Autowired
    private OrderPushAbility orderPushAbility;

    @Autowired
    private OrderRollCouponService orderRollCouponService;

    @Autowired
    private OrderAutoCancelConfigService orderAutoCancelConfigService;

    /**
     * 发起支付
     * @param orderPayRpcReq
     * @return
     * 2025-3-22 10:34:07
     */
    @Override
    public RpcResponse<OrderPayRpcResp> toPay(OrderPayRpcReq orderPayRpcReq) {
        TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(orderPayRpcReq.getPrimaryOrderId());
        // 必须存在
        if (Objects.isNull(tcOrderDO)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        // 创建状态
        if (OrderStatusEnum.CREATED.getCode().equals(tcOrderDO.getPrimaryOrderStatus())) {
            // 修改为待支付
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                orderPayRpcReq.getPrimaryOrderId(),
                OrderStatusEnum.WAITING_FOR_PAYMENT.getCode(),
                null,
                OrderStatusEnum.CREATED.getCode()
            );
            // 写流水
            TcOrderOperateFlowDO flow = getOpFlow(
                tcOrderDO,
                OrderChangeOperateEnum.WAITING_FOR_PAYMENT,
                OrderStatusEnum.WAITING_FOR_PAYMENT.getCode()
            );
            if(Objects.nonNull(flow)) {
                tcOrderOperateFlowRepository.create(flow);
            }
            // 查一下订单
            MainOrder mainOrder = orderQueryAbility.getMainOrder(orderPayRpcReq.getPrimaryOrderId());
            // 创建任务 -- 自动取消使用
            orderTaskAbility.orderTask(mainOrder,mainOrder.getPrimaryOrderStatus());
        }
        // 开始发起支付
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", orderPayRpcReq);
        OrderPayRpcResp result = null;
        FailInfo fail = null;
        try {
            result = (OrderPayRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getToPay(),
                toPayContext
            );
        } catch (GmallException e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(e.getFrontendCare());
        } catch (Exception e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR);
        }
        //发起支付行为日志
        OpLogCommonUtil.update(
            fail == null,
            orderPayRpcReq.getClientInfo(),
            AppIdConstants.TRADE_CENTER, BizTypeConstants.TO_PAY,
            orderPayRpcReq.getPrimaryOrderId().toString()
        );
        if (fail != null) {
            return RpcResponse.fail(fail);
        }
        if(Objects.nonNull(result) && StringUtils.isNotBlank(result.getCartId())) {
            // 更新支付的支付标识符和银行卡号
            tcOrderRepository.updateCartIdAndBankIdByPrimaryIds(
                List.of(orderPayRpcReq.getPrimaryOrderId()),
                result.getCartId(),
                orderPayRpcReq.getBankCardNbr()
            );
        }
        return RpcResponse.ok(result);
    }

    /**
     * 操作流水
     * @param order
     * @param op
     * @param status
     * @return
     */
    private TcOrderOperateFlowDO getOpFlow(TcOrderDO order, OrderChangeOperate op, Integer status) {
        if(order.getPrimaryOrderFlag() == 1 ) {
            TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
            flow.setOrderId(order.getOrderId());
            flow.setPrimaryOrderId(order.getPrimaryOrderId());
            flow.setToOrderStatus(status);
            flow.setOperator(String.valueOf(order.getCustId()));
            flow.setOperatorType(op.getOprType());
            flow.setOpName(op.getOpName());
            flow.setFromOrderStatus(order.getPrimaryOrderStatus());
            return flow;
        }
        return null;
    }
    /**
     * 操作流水
     * @param order
     * @param op
     * @param status
     * @return
     */
    private TcOrderOperateFlowDO getOpFlow(MainOrder order, OrderChangeOperate op, Integer status) {

        TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
        flow.setOrderId(order.getPrimaryOrderId());
        flow.setPrimaryOrderId(order.getPrimaryOrderId());
        flow.setToOrderStatus(status);
        if (Objects.nonNull(order.getCustomer())) {
            flow.setOperator(String.valueOf(order.getCustomer().getCustId()));
        }
        flow.setOperatorType(op.getOprType());
        flow.setOpName(op.getOpName());
        flow.setFromOrderStatus(order.getPrimaryOrderStatus());
        return flow;

    }

    /**
     * 合并支付
     * @param orderMergePayRpcReq
     * @return
     */
    @Override
    public RpcResponse<OrderMergePayRpcResp> toMergePay(OrderMergePayRpcReq orderMergePayRpcReq) {
        List<TcOrderOperateFlowDO> flowList = new ArrayList<>();
        for (Long primaryOrderId : orderMergePayRpcReq.getPrimaryOrderIds()) {
            TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
            // 必须存在
            if (Objects.isNull(tcOrderDO)) {
                throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
            }
            if (OrderStatusEnum.CREATED.getCode().equals(tcOrderDO.getPrimaryOrderStatus())) {
                tcOrderRepository.updateStatusAndStageByPrimaryId(
                    primaryOrderId,
                    OrderStatusEnum.WAITING_FOR_PAYMENT.getCode(),
                    null,
                    OrderStatusEnum.CREATED.getCode()
                );
                // 写流水
                TcOrderOperateFlowDO flow = getOpFlow(
                    tcOrderDO,
                    OrderChangeOperateEnum.WAITING_FOR_PAYMENT,
                    OrderStatusEnum.WAITING_FOR_PAYMENT.getCode()
                );
                if(Objects.nonNull(flow)) {
                    flowList.add(flow);
                }

                // 查一下订单
                MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
                // 创建任务 -- 自动取消
                orderTaskAbility.orderTask(mainOrder,mainOrder.getPrimaryOrderStatus());
            }
        }
        if(!CollectionUtils.isEmpty(flowList)) {
            tcOrderOperateFlowRepository.batchCreate(flowList);
        }
        Map<String, Object> toPayContext = new HashMap<>();
        toPayContext.put("req", orderMergePayRpcReq);
        OrderMergePayRpcResp result = null;
        FailInfo fail = null;
        try {
            result = (OrderMergePayRpcResp) workflowEngine.invokeAndGetResult(
                workflowProperties.getToMergePay(),
                toPayContext
            );
        } catch (GmallException e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(e.getFrontendCare());
        } catch (Exception e) {
            log.error("OrderPayWriteFacadeImpl.toPay return occurred exceptions!", e);
            fail = ErrorUtils.getFailInfo(CommonErrorCode.SERVER_ERROR);
        }
        //发起支付行为日志
        for (Long primaryOrderId : orderMergePayRpcReq.getPrimaryOrderIds()) {
            OpLogCommonUtil.update(
                fail == null,
                orderMergePayRpcReq.getClientInfo(),
                AppIdConstants.TRADE_CENTER,
                "TO_MERGE_PAY",
                primaryOrderId.toString()
            );
        }
        if (fail != null) {
            return RpcResponse.fail(fail);
        }
        if(Objects.nonNull(result) && StringUtils.isNotBlank(result.getCartId())) {
            tcOrderRepository.updateCartIdAndBankIdByPrimaryIds(
                orderMergePayRpcReq.getPrimaryOrderIds(),
                result.getCartId(),
                orderMergePayRpcReq.getBankCardNbr()
            );
        }
        return RpcResponse.ok(result);
    }

    /**
     * 支付失败 自动退单
     * @param primaryOrderIds
     * @return
     */
    public RpcResponse<Boolean> cancelOrders(List<Long> primaryOrderIds) {
        if (CollectionUtils.isEmpty(primaryOrderIds)) {
            log.warn("cancelOrder primaryOrderList is empty");
            return RpcResponse.ok(false);
        }
        MainOrder mainOrder = null;
        List<TcOrderOperateFlowDO> flowList = new ArrayList<>();
        List<MainOrder> mainOrdersList = new ArrayList<>();
        for (Long primaryOrderId : primaryOrderIds) {
            MainOrder mainOrderOld = orderQueryAbility.getMainOrder(primaryOrderId);
            // 必须存在
            if (Objects.isNull(mainOrderOld)) {
                throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
            }
            // 等待支付
            if (OrderStatusEnum.WAITING_FOR_PAYMENT.getCode().equals(mainOrderOld.getPrimaryOrderStatus())) {
                // 订单扩展字段
                OrderAttrDO orderAttrDO = Objects.isNull(mainOrder.getOrderAttr()) ? new OrderAttrDO() : mainOrder.getOrderAttr();
                // 自动退单的原因说明
                orderAttrDO.setRemark("");
                // 根据订单状态读取配置表，找到对应的取消原因
                orderAttrDO.setReasonCode(orderAutoCancelConfigService.getCancelCodeByOrderStatus(mainOrderOld.getPrimaryOrderStatus()));
                TcOrderDO newTcorderDO = new TcOrderDO();
                newTcorderDO.setOrderAttr(orderAttrDO);
                newTcorderDO.setPrimaryOrderId(mainOrderOld.getPrimaryOrderId());
                newTcorderDO.setOrderId(mainOrderOld.getPrimaryOrderId());
                newTcorderDO.setVersion(mainOrder.getVersion());
                tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
                // 待支付修复为取消
                tcOrderRepository.updateStatusAndStageByPrimaryId(
                    primaryOrderId,
                    OrderStatusEnum.CANCELLED.getCode(),
                    null,
                    OrderStatusEnum.WAITING_FOR_PAYMENT.getCode()
                );
                // 写流水
                TcOrderOperateFlowDO flow = getOpFlow(
                    mainOrderOld,
                    OrderChangeOperateEnum.CANCELLED,
                    OrderStatusEnum.CANCELLED.getCode()
                );
                flowList.add(flow);
                mainOrdersList.add(mainOrderOld);
                // 查一下订单
                mainOrder = Objects.isNull(mainOrder) ? orderQueryAbility.getMainOrder(primaryOrderId) : mainOrder;
            }
        }
        // 写流水
        if(!CollectionUtils.isEmpty(flowList)) {
            tcOrderOperateFlowRepository.batchCreate(flowList);
        }
        // 退券
        orderRollCouponService.orderRollCoupon(mainOrder);
        // 发消息
        if(!CollectionUtils.isEmpty(mainOrdersList))
            for(MainOrder order: mainOrdersList) {
                //给客户发PUSH,当前状态为61待支付，是为了推送消息根据主单状态获取准确的模版
                orderPushAbility.send(order, ImTemplateTypeEnum.PUSH.getCode());
            }
        return RpcResponse.ok(true);
    }
}
