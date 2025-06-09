package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.CustDelOrderCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderStatusAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.AutoEvaluateOrderTask;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.SystemConfirmOrderTask;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class OrderStatusServiceImpl implements OrderStatusService {

    @Autowired
    OrderStatusAbility orderStatusAbility;
    @Autowired
    TcOrderRepository tcOrderRepository;
    @Autowired
    CustDelOrderCheckAbility custDelOrderCheckAbility;
    @Autowired
    AutoEvaluateOrderTask autoEvaluateOrderTask;
    @Autowired
    SystemConfirmOrderTask systemConfirmOrderTask;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private OrderInventoryAbility orderInventoryAbility;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;
    @Autowired
    private StepOrderDomainService stepOrderDomainService;
    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;
    @Autowired
    private OrderPayRepository orderPayRepository;
    @Autowired
    private OrderPushAbility orderPushAbility;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private I18NConfig i18NConfig;
    @Autowired
    private OrderRefundAbility orderRefundAbility;

    public TradeBizResult<List<TcOrderDO>> updateStatus(OrderStatus orderStatus) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        orderAttrDO.setReasonCode(orderStatus.getReasonCode());
        TcOrderDO newTcorderDO = new TcOrderDO();
        newTcorderDO.setOrderAttr(orderAttrDO);
        newTcorderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setVersion(mainOrder.getVersion());
        newTcorderDO.setCancelCode(orderStatus.getReasonCode());
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));

    }

    /**
     * 订单状态修改
     * @param orderStatus
     * @return
     */
    public TradeBizResult<List<TcOrderDO>> changeOrderStatus(OrderStatus orderStatus) {
        //顾客取消订单实现
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (Objects.isNull(mainOrder)) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        Integer primaryOrderStatus = mainOrder.getPrimaryOrderStatus();
        //商家接收 物流方式是商家物流不可取消
        if(Objects.equals(primaryOrderStatus, OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode())
        && (mainOrder.getOrderAttr().getLogisticsType().equals(LogisticsTypeEnum.COURIER_LODOOR.getCode()))){
            throw new GmallException(OrderErrorCode.ORDER_NOTCANCEL_MERCHANT_LOGISTICS);
        }
        if(Objects.equals(primaryOrderStatus, OrderStatusEnum.CANCELLED.getCode())
        ||Objects.equals(primaryOrderStatus, OrderStatusEnum.CANCEL_FAILED.getCode())) {
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ERROR);
        }
        Boolean isReFund = false;
        // 理论上 不会出现 创建状态
        if (Objects.equals(primaryOrderStatus, OrderStatusEnum.CREATED.getCode())) {
            if (!((orderStatus.getStatus() != null &&
                Objects.equals(orderStatus.getStatus().getCode(), OrderStatusEnum.CREATED.getCode()))
                &&
                (orderStatus.getCheckStatus() != null &&
                Objects.equals(orderStatus.getCheckStatus().getCode(), OrderStatusEnum.CREATED.getCode())))) {
                orderStatus.setCheckStatus(OrderStatusEnum.CREATED);
                orderStatus.setStatus(OrderStatusEnum.CANCELLED);
                // 没有库存
                //orderInventoryAbility.rollbackInventoryBeforePay(List.of(mainOrder));
            }
        //  确认支付
        } else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.PAYMENT_CONFIRMED.getCode())){
            orderStatus.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMED);
            isReFund = true;
        //  商家确认订单
        }else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode())){
            orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
            isReFund = true;
        //  等待支付
        }else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.WAITING_FOR_PAYMENT.getCode())){
            orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_PAYMENT);
            //定金支付
        }else if(Objects.equals(primaryOrderStatus, OrderStatusEnum.PARTIALLY_PAID.getCode())){
            orderStatus.setCheckStatus(OrderStatusEnum.PARTIALLY_PAID);
            isReFund = true;
        }
        orderStatus.setCancelFromStatus(OrderStatusEnum.codeOf(primaryOrderStatus));
        orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
        // 操作写流水
        addFlow(mainOrder, OrderStatusEnum.CANCEL_REQUESTED.getCode(), OrderStatusEnum.CANCEL_REQUESTED.getInner(),false);
        // 退款原因
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        orderAttrDO.setReasonCode(orderStatus.getReasonCode());
        // 变更订单的信息
        TcOrderDO newTcorderDO = new TcOrderDO();
        newTcorderDO.setOrderAttr(orderAttrDO);
        newTcorderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setVersion(mainOrder.getVersion());
        newTcorderDO.setCancelCode(orderStatus.getReasonCode());
        // 更新订单
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        //查询最新状态
        MainOrder newMainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        // 发起退款
        if(isReFund){
            //首先更新CANCEL_REQUESTED
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                orderStatus.getPrimaryOrderId(),
                orderStatus.getStatus().getCode(),
                orderStatus.getOrderStage(),
                orderStatus.getCheckStatus().getCode()
            );
            orderRefundAbility.doRefund(orderStatus, newMainOrder);
        }else{
            orderStatus.setStatus(OrderStatusEnum.CANCELLED);
            //orderStatus.setCheckStatus(OrderStatusEnum.CANCEL_REQUESTED);
        }
        // 整单退券
        promotionRepository.orderRollbackUserAssets(mainOrder);
        // 订单状态修改扩展事件
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(newMainOrder));
    }

    /**
     * 写操作流水
     * @param mainOrder
     * @param targetStatus
     * @param name
     * @param isCustomer
     */
    protected void addFlow(MainOrder mainOrder, Integer targetStatus, String name, boolean isCustomer) {
        List<TcOrderOperateFlowDO> flows = new ArrayList<>();
        for(SubOrder subOrder : mainOrder.getSubOrders()){
            OrderOperateFlowQuery query = new OrderOperateFlowQuery();
            query.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            query.setFromOrderStatus(mainOrder.getPrimaryOrderStatus());
            query.setToOrderStatus(targetStatus);
            if(!tcOrderOperateFlowRepository.exist(query)) {
                TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
                flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                flow.setOrderId(subOrder.getOrderId());
                flow.setFromOrderStatus(mainOrder.getPrimaryOrderStatus());
                flow.setToOrderStatus(targetStatus);
                flow.setOperatorType(isCustomer ? 1 : 0);
                flow.setGmtCreate(new Date());
                flow.setOpName(name);
                flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
                flow.setGmtModified(new Date());
                flows.add(flow);
            }
        }
        tcOrderOperateFlowRepository.batchCreate(flows);
    }

    /*@Override
    @Transactional
    public TradeBizResult<List<TcOrderDO>> changeOrderStatus(OrderStatus orderStatus) {
        //顾客取消订单实现
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        *//**
         * -- 当订单状态为ORDER_WAIT_PAY时，设置checkStatus为ORDER_WAIT_PAY，status为ORDER_BUYER_CANCEL
         * 当订单状态为WAIT_SELLER_RECEIVED时，设置checkStatus为WAIT_SELLER_RECEIVED，status为SELLER_CANCELING
         * 当订单状态为SELLER_CONFIRMED时，设置checkStatus为SELLER_CONFIRMED，status为SELLER_CANCELING
         *
         * 当订单状态为CREATED,取消订单时，设置checkStatus为CANCELLED，status为CANCELLED
         *//*
        Integer primaryOrderStatus = mainOrder.getPrimaryOrderStatus();
        if (Objects.equals(primaryOrderStatus, OrderStatusEnum.CREATED.getCode())) {
            if (!((orderStatus.getStatus() != null
                    && Objects.equals(orderStatus.getStatus().getCode(), OrderStatusEnum.CREATED.getCode()))
                    && (orderStatus.getCheckStatus() != null
                    && Objects.equals(orderStatus.getCheckStatus().getCode(), OrderStatusEnum.CREATED.getCode())))) {
                orderStatus.setCheckStatus(OrderStatusEnum.CREATED);
                orderStatus.setStatus(OrderStatusEnum.CANCELLED);
            }
        } else if (Objects.equals(primaryOrderStatus, OrderStatusEnum.WAIT_SELLER_RECEIVED.getCode())
                || Objects.equals(primaryOrderStatus, OrderStatusEnum.SELLER_CANCELING.getCode())) {
            orderStatus.setCheckStatus(OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()));
            if (StringUtils.equals(orderStatus.getExtra().get(CancelOrderAttribute.NEED_OFFLINE_REFUND), "false")) {
                orderStatus.setStatus(OrderStatusEnum.ORDER_BUYER_CANCEL);
            } else {
                orderStatus.setStatus(OrderStatusEnum.SELLER_CANCELING);
            }
        }
        if (orderStatus.getCustId() != null &&
                !orderStatus.getCustId().equals(mainOrder.getCustomer().getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (orderStatus.getSellerId() != null &&
                !orderStatus.getSellerId().equals(mainOrder.getSeller().getSellerId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }

        if (orderStatus.getStatus() == OrderStatusEnum.ORDER_CONFIRM ||
                orderStatus.getStatus() == OrderStatusEnum.SYSTEM_CONFIRM ||
                orderStatus.getStatus() == OrderStatusEnum.ORDER_SENDED) {
            OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
            if (orderAttrDO == null) {
                orderAttrDO = new OrderAttrDO();
            }
            if (orderStatus.getStatus() == OrderStatusEnum.ORDER_CONFIRM ||
                    orderStatus.getStatus() == OrderStatusEnum.SYSTEM_CONFIRM) {
                orderAttrDO.setConfirmReceiveTime(new Date());
                ScheduleTask task = autoEvaluateOrderTask.buildTask(new TaskParam(
                        mainOrder.getSeller().getSellerId(),
                        mainOrder.getPrimaryOrderId(),
                        BizCodeEntity.getOrderBizCode(mainOrder)));
                orderTaskService.createScheduledTask(Lists.newArrayList(task));
                orderStatus.setOrderStage(OrderStageEnum.AFTER_SALE.getCode());
            }
            if (orderStatus.getStatus() == OrderStatusEnum.ORDER_SENDED) {
                SysConfirmTaskParam p = new SysConfirmTaskParam();
                p.setSellerId(mainOrder.getSeller().getSellerId());
                p.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
                p.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
                ScheduleTask task = systemConfirmOrderTask.buildTask(p);
                orderTaskService.createScheduledTask(Lists.newArrayList(task));
                orderAttrDO.setSendTime(new Date());
            }
            TcOrderDO newTcorderDO = new TcOrderDO();
            newTcorderDO.setOrderAttr(orderAttrDO);
            newTcorderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
            newTcorderDO.setOrderId(orderStatus.getPrimaryOrderId());
            newTcorderDO.setVersion(mainOrder.getVersion());
            boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }

        // 售前关闭订单，关闭支付单、回补库存
        if (OrderStatusEnum.codeOf(primaryOrderStatus) == OrderStatusEnum.WAIT_SELLER_RECEIVED) {
            Map<String, String> extra = orderStatus.getExtra();
            log.info("传入额外信息为：{}",extra.toString());
            log.info("是否需要退款：{}",orderStatus.getExtra().get(CancelOrderAttribute.NEED_OFFLINE_REFUND).toString());
            if (StringUtils.equals(orderStatus.getExtra().get(CancelOrderAttribute.NEED_OFFLINE_REFUND), "false")) {
                log.info("cancel pay!");
                orderPayRepository.cancelPay(mainOrder, mainOrder.orderAttr().getCurrentStepNo());
                try {
                    log.info("库存回补");
                    orderInventoryAbility.rollbackInventoryBeforePay(Arrays.asList(mainOrder));
                } catch (Exception e) {
                    log.warn("ignore rollbackInventoryBeforePay fail, orderId: {}", mainOrder.getPrimaryOrderId(), e);
                }
            } else if (StringUtils.equals(orderStatus.getExtra().get(CancelOrderAttribute.NEED_OFFLINE_REFUND), "true")) {
                //修改应付应收单状态为"支付取消中"
                log.info("update pay status to canceling!");
                orderPayRepository.updatePayStatus(mainOrder, PayStatusEnum.PENDING_REFUND.getCode());
            }
        }
        if ((OrderStageEnum.codeOf(mainOrder.getOrderAttr().getOrderStage()) == OrderStageEnum.BEFORE_SALE
                || OrderStageEnum.codeOf(mainOrder.getOrderAttr().getOrderStage()) == OrderStageEnum.ON_SALE)
                && (orderStatus.getStatus() == OrderStatusEnum.SYSTEM_CLOSE
                || orderStatus.getStatus() == OrderStatusEnum.ORDER_SELLER_CLOSE
                || orderStatus.getStatus() == OrderStatusEnum.ORDER_BUYER_CANCEL)) {
            log.info("cancel pay!");
            orderPayRepository.cancelPay(mainOrder, mainOrder.orderAttr().getCurrentStepNo());

            InventoryReduceType invType = InventoryReduceType.codeOf(mainOrder.getOrderAttr().getInventoryReduceType());
            if (invType == InventoryReduceType.REDUCE_ON_ORDER
                    || orderStatus.getStatus() == OrderStatusEnum.ORDER_BUYER_CANCEL) {
                // 回补
                try {
                    log.info("rollback inventory before pay!");
                    orderInventoryAbility.rollbackInventoryBeforePay(Arrays.asList(mainOrder));
                } catch (Exception e) {
                    log.warn("ignore rollbackInventoryBeforePay fail, orderId: {}", mainOrder.getPrimaryOrderId(), e);
                }
            } else if (invType == InventoryReduceType.REDUCE_ON_PAY) {
                // 解除锁定
                try {
                    log.info("unlock inventory!");
                    orderInventoryAbility.unlockInventory(Arrays.asList(mainOrder));
                } catch (Exception e) {
                    log.warn("ignore unlockInventory fail, orderId: {}", mainOrder.getPrimaryOrderId(), e);
                }
            }
        }

        //如果extra不为空则写入order表
        if (MapUtils.isNotEmpty(orderStatus.getExtra())) {
            Map<String, String> extras = mainOrder.getOrderAttr().getExtras();
            extras.remove(CancelOrderAttribute.NEED_OFFLINE_REFUND);
            extras.remove(CancelOrderAttribute.CANCEL_AMT_YUAN);
            extras.remove(CancelOrderAttribute.CANCEL_REASON);
            extras.remove(CancelOrderAttribute.CANCEL_PHOTO);
            if (orderStatus.getExtra().containsKey(CancelOrderAttribute.NEED_OFFLINE_REFUND)) {
                extras.put(CancelOrderAttribute.NEED_OFFLINE_REFUND, orderStatus.getExtra().get(CancelOrderAttribute.NEED_OFFLINE_REFUND));
            }
            if (orderStatus.getExtra().containsKey(CancelOrderAttribute.CANCEL_AMT_YUAN)) {
                extras.put(CancelOrderAttribute.CANCEL_AMT_YUAN, orderStatus.getExtra().get(CancelOrderAttribute.CANCEL_AMT_YUAN));
            }
            if (orderStatus.getExtra().containsKey(CancelOrderAttribute.CANCEL_REASON)) {
                extras.put(CancelOrderAttribute.CANCEL_REASON, orderStatus.getExtra().get(CancelOrderAttribute.CANCEL_REASON));
            }
            if (orderStatus.getExtra().containsKey(CancelOrderAttribute.CANCEL_PHOTO)) {
                extras.put(CancelOrderAttribute.CANCEL_PHOTO, orderStatus.getExtra().get(CancelOrderAttribute.CANCEL_PHOTO));
            }
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setOrderAttr(mainOrder.getOrderAttr());
            newTcOrderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
            newTcOrderDO.setOrderId(orderStatus.getPrimaryOrderId());
            newTcOrderDO.setVersion(mainOrder.getVersion());
            boolean success = tcOrderRepository.updateByOrderIdVersion(newTcOrderDO);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
        if(mainOrder.getPrimaryOrderStatus()!=null){
            orderStatus.setCheckStatus(OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()));
        }
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
    }*/

    @Override
    public void customerDeleteOrder(Long primaryId) {
        tcOrderRepository.customerDeleteOrder(primaryId);
    }

    @Override
    public TradeBizResult<Boolean> custDelOrderCheck(Long primaryId) {
        return custDelOrderCheckAbility.check(primaryId);
    }

    @Override
    public TradeBizResult<Boolean> custDelOrderCheck(Long primaryId, Long custId) {
        return custDelOrderCheckAbility.check(primaryId, custId);
    }

    @Override
    public void onPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        LocaleContextHolder.setLocale(new Locale(i18NConfig.getDefaultLang()));
        // 更新支付渠道
        if (!StringUtils.equals(mainOrder.orderAttr().getPayChannel(), orderPay.getPayChannel())) {
            mainOrder.orderAttr().setPayChannel(orderPay.getPayChannel());
            TcOrderDO up = new TcOrderDO();
            up.setOrderId(mainOrder.getPrimaryOrderId());
            up.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            up.setOrderAttr(mainOrder.orderAttr());
            up.setVersion(mainOrder.getVersion());
            if (!tcOrderRepository.updateByOrderIdVersion(up)) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            mainOrder.setVersion(up.getVersion());
        }
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            stepOrderDomainService.onPaySuccess(mainOrder, orderPay);
            //定金尾款支付
            if(Objects.equals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.PARTIALLY_PAID.getCode())){
                addFlow(mainOrder,OrderStatusEnum.PAYMENT_CONFIRMED.getCode(),OrderStatusEnum.PAYMENT_CONFIRMED.getInner(),false);
            }else {
                addFlow(mainOrder,OrderStatusEnum.PARTIALLY_PAID.getCode(),OrderStatusEnum.PARTIALLY_PAID.getInner(),false);
            }
            return;
        }
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        List<TcOrderOperateFlowDO> list = new ArrayList<>();
       /* if(null != mainOrder.getPrimaryOrderStatus() && mainOrder.getPrimaryOrderStatus().equals(OrderStatusEnum.WAITING_FOR_PAYMENT.getCode())){
            TcOrderOperateFlowDO temp =
            OrderChangedNotifyServiceImpl.buildOrderPay(
                    mainOrder, OrderStatusEnum.WAITING_FOR_PAYMENT, OrderStatusEnum.PAYMENT_CONFIRMING);
            list.add(temp);
        }*/
        list.add(
            OrderChangedNotifyServiceImpl.buildOrderPay(
                mainOrder,
                OrderStatusEnum.WAITING_FOR_PAYMENT,
                OrderStatusEnum.PAYMENT_CONFIRMED
            )
        );

        Integer status = orderStatusAbility.getOrderStatusOnPaySuccess(mainOrder, orderPay);

        tcOrderRepository.updateStatusAndStageAndCartIdByPrimaryId(
            primaryOrderId,
            status,
            OrderStageEnum.ON_SALE.getCode(),
            null,
            orderPay.getPayCartId()
        );

        MainOrder mainOrderUp = orderQueryAbility.getMainOrder(primaryOrderId);

        orderChangedNotifyService.afterStatusChange(
            OrderChangeNotify.builder()
            .mainOrder(mainOrderUp)
            .flows(list)
            .op(OrderChangeOperateEnum.CUST_PAY_COMFIRM)
            .build()
        );
    }


    @Override
    public void payComfirming(MainOrder mainOrder) {
        LocaleContextHolder.setLocale(new Locale(i18NConfig.getDefaultLang()));
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        OrderStatusEnum checkStatus = OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus());
        List<TcOrderOperateFlowDO> list = Lists.newArrayList(
            OrderChangedNotifyServiceImpl.buildOrderPay(
                mainOrder,
                checkStatus,
                OrderStatusEnum.PAYMENT_CONFIRMING
            )
        );
        tcOrderRepository.updateStatusAndStageByPrimaryId(
            primaryOrderId,
            OrderStatusEnum.PAYMENT_CONFIRMING.getCode(),
            OrderStageEnum.ON_SALE.getCode(),
            checkStatus.getCode()
        );
        MainOrder mainOrderUp = orderQueryAbility.getMainOrder(primaryOrderId);
        orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
            .mainOrder(mainOrderUp)
            .flows(list)
            .op(OrderChangeOperateEnum.CUST_PAY_COMFIRMING)
            .build());
    }

    @Override
    public TradeBizResult<List<TcOrderDO>> changeStepOrderStatus(OrderStatus orderStatus) {
        //判断订单是否存在
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder || null == mainOrder.getPrimaryOrderStatus()) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        //判断订单尾款支付，如果是定金支付或者支付中，则更改状态。
        Integer primaryOrderStatus = mainOrder.getPrimaryOrderStatus();
        if (OrderStatusEnum.PAYMENT_CONFIRMING.getCode().equals(primaryOrderStatus) || OrderStatusEnum.PARTIALLY_PAID.getCode().equals(primaryOrderStatus)) {
            orderStatus.setCheckStatus(OrderStatusEnum.codeOf(primaryOrderStatus));
            return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
        }
        if (OrderStatusEnum.PAYMENT_CONFIRMED.getCode().equals(primaryOrderStatus)) {
            log.warn("changeStepOrderStatus is confirmed primaryOrderStatus={},orderStatus={}", primaryOrderStatus, orderStatus);
            return TradeBizResult.ok();
        }
        log.warn("changeStepOrderStatus primaryOrderStatus={},orderStatus={}", primaryOrderStatus, orderStatus);
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
    }
}