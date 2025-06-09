package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.BaseStepOperate;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.FormAction;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.SellerOperate;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import com.aliyun.gts.gmall.platform.trade.common.util.MoneyUtils;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.OrderChangedNotifyServiceImpl;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.StepOrderTimeoutTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.StepTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

import static com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils.getNullZero;

@Slf4j
@Component
public class DefaultStepOrderProcessExt implements StepOrderProcessExt {

    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;
    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private TransactionProxy transactionProxy;
    @Autowired
    private OrderPriceService orderPriceService;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private StepOrderTimeoutTask stepOrderTimeoutTask;
    @Autowired
    private OrderTaskService orderTaskService;

    @Override
    public void onPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        List<OrderChangeNotify> messageList = new ArrayList<>();
        //事务
        transactionProxy.callTx(() -> {
            MainOrder main = mainOrder;
            if (OrderStatusEnum.codeOf(main.getPrimaryOrderStatus()) == OrderStatusEnum.WAITING_FOR_PAYMENT) {
                main = updateOrderPayStatus(main, messageList);
            }
            main.getCurrentStepOrder().features().setPayTime(System.currentTimeMillis());
            main.getCurrentStepOrder().features().setPayCartId(orderPay.getPayCartId());
            saveNextStep(main, messageList, OrderChangeOperateEnum.STEP_CUST_PAY);
            //savePaidAmt(main);
            afterPaySuccessTx(mainOrder, orderPay);
        });
        for (OrderChangeNotify message : messageList) {
            orderChangedNotifyService.afterStatusChange(message);
            break;
            //只处理一次push
        }
    }

    protected void afterPaySuccessTx(MainOrder mainOrder, OrderPay orderPay) {

    }

    @Override
    public void onSellerHandle(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        if (req.isModify()) {
            onSellerModify(mainOrder, req);
            return;
        }

        checkOperate(mainOrder, req, StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE);
        List<OrderChangeNotify> messageList = new ArrayList<>();
        transactionProxy.callTx(() -> {
            mainOrder.getCurrentStepOrder().features().setSellerHandleTime(System.currentTimeMillis());
            saveFormData(mainOrder, req.getFormData());
            doSellerHandleAction(mainOrder, req, messageList);
            saveNextStep(mainOrder, messageList, OrderChangeOperateEnum.STEP_SELLER_HANDLE);
            afterSellerHandleTx(mainOrder, req);
        });
        for (OrderChangeNotify message : messageList) {
            orderChangedNotifyService.afterStatusChange(message);
        }
    }

    protected void afterSellerHandleTx(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        // 扩展
    }

    protected void onSellerModify(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        checkOperate(mainOrder, req, StepOrderStatusEnum.STEP_WAIT_CONFIRM);
        List<OrderChangeNotify> messageList = new ArrayList<>();
        transactionProxy.callTx(() -> {
            saveFormData(mainOrder, req.getFormData());
            doSellerHandleAction(mainOrder, req, messageList);
            afterSellerModifyTx(mainOrder, req);
        });
        for (OrderChangeNotify message : messageList) {
            orderChangedNotifyService.afterStatusChange(message);
        }
    }

    protected void afterSellerModifyTx(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        // 扩展
    }

    protected void doSellerHandleAction(MainOrder mainOrder, StepOrderHandleRpcReq req, List<OrderChangeNotify> messageList) {
        int stepNo = mainOrder.orderAttr().getCurrentStepNo();
        StepMeta stepMeta = mainOrder.getStepTemplate().getStepMeta(stepNo);
        SellerOperate op = stepMeta.getSellerOperate();
        if (!op.isNeed() || CollectionUtils.isEmpty(op.getFormData())) {
            return;
        }
        List<FormAction> actions = op.getAction();
        if (CollectionUtils.isEmpty(actions)) {
            return;
        }

        for (FormAction action : actions) {
            dispatchSellerHandleAction(action, mainOrder, req, messageList);
        }
    }

    protected void dispatchSellerHandleAction(FormAction action,
                                              MainOrder mainOrder,
                                              StepOrderHandleRpcReq req,
                                              List<OrderChangeNotify> messageList) {
        // 改价
        if ("ADJUEST_FEE".equals(action.getName())) {
            adjustFee(action, mainOrder, req, messageList);
        }
        // 其他action 扩展实现
        else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "not support: " + action.getName());
        }
    }

    protected void adjustFee(FormAction action,
                             MainOrder mainOrder,
                             StepOrderHandleRpcReq req,
                             List<OrderChangeNotify> messageList) {
        Integer stepNo = action.getInt("stepNo", null);
        String targetFeeKey = action.getObject("targetFee");
        String freightFeeKey = action.getObject("freightFee");
        if (stepNo == null || targetFeeKey == null || freightFeeKey == null) {
            throw new GmallException(OrderErrorCode.STEP_TEMPLATE_ERROR);
        }
        // 目标价格
        String targetFeeYuan = mainOrder.orderAttr().getStepContextProps().get(targetFeeKey);
        String freightFeeYuan = mainOrder.orderAttr().getStepContextProps().get(freightFeeKey);

        // 计算调整金额
        Long adjFreightFee = null;
        Long adjRealFee = null;

        long freightFee = getNullZero(mainOrder.getOrderPrice().getFreightAmt());
        if (MoneyUtils.checkMoneyYuan(freightFeeYuan)) {
            freightFee = Long.valueOf(freightFeeYuan);
            adjFreightFee = freightFee - getNullZero(mainOrder.getOrderPrice().getFreightAmt());
        }
        if (MoneyUtils.checkMoneyYuan(targetFeeYuan)) {
            StepOrder stepOrder = StepOrderUtils.getStepOrder(mainOrder, stepNo);
            long targetFee = Long.valueOf(targetFeeYuan);  // 阶段的目标商品价格
            long realFee = targetFee + freightFee - getNullZero(stepOrder.getPrice().getPointAmt());  // 阶段的目标现金实付
            adjRealFee = realFee - getNullZero(stepOrder.getPrice().getRealAmt());   // 实付现金调整, 含商品+运费
        }

        if (getNullZero(adjRealFee) != 0
                || getNullZero(adjFreightFee) != 0) {
            // 执行改价, 默认全部改现金部分
            AdjustPrice adj = new AdjustPrice();
            adj.setStepNo(stepNo);
            adj.setAdjustFreightAmt(adjFreightFee);
            adj.setAdjustRealAmt(adjRealFee);
            adj.setAdjustPromotionAmt(getNullZero(adjRealFee) - getNullZero(adjFreightFee));
            messageList.addAll(orderPriceService.adjustPrice(mainOrder, adj));
        }
    }

    @Override
    public void onCustomerConfirm(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        checkOperate(mainOrder, req, StepOrderStatusEnum.STEP_WAIT_CONFIRM);
        checkFormDate(req.getFormData(), mainOrder);

        List<OrderChangeNotify> messageList = new ArrayList<>();
        transactionProxy.callTx(() -> {
            mainOrder.getCurrentStepOrder().features().setCustomerConfirmTime(System.currentTimeMillis());
            saveNextStep(mainOrder, messageList, OrderChangeOperateEnum.STEP_CUST_CONFIRM);
            afterCustomerConfirmTx(mainOrder, req);
        });
        for (OrderChangeNotify message : messageList) {
            orderChangedNotifyService.afterStatusChange(message);
        }
    }

    protected void afterCustomerConfirmTx(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        // 扩展
    }

    protected void checkOperate(MainOrder mainOrder, StepOrderHandleRpcReq req, StepOrderStatusEnum checkStatus) {
        if (req.getSellerId() != null && mainOrder.getSeller().getSellerId().longValue() != req.getSellerId().longValue()) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (req.getCustId() != null && mainOrder.getCustomer().getCustId().longValue() != req.getCustId().longValue()) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (req.getCheckVersion() != null && mainOrder.getVersion().longValue() != req.getCheckVersion().longValue()) {
            throw new GmallException(OrderErrorCode.ORDER_CHANGED_ON_CONFIRM);
        }
        if (!StepOrderUtils.isMultiStep(mainOrder)) {
            throw new GmallException(OrderErrorCode.NOT_MULTI_STEP_ORDER);
        }
        if (mainOrder.getOrderAttr().getCurrentStepNo().intValue() != req.getStepNo().intValue()) {
            throw new GmallException(OrderErrorCode.STEP_NO_ILLEGAL);
        }
        StepOrderStatusEnum status = StepOrderStatusEnum.codeOf(mainOrder.getCurrentStepOrder().getStatus());
        if (status != checkStatus) {
            throw new GmallException(OrderErrorCode.STEP_STATUS_ILLEGAL);
        }
    }

    protected void checkFormDate(Map<String, String> formData, MainOrder mainOrder) {
        if (formData == null) {
            return;
        }
        Map<String, String> stepContext = mainOrder.orderAttr().getStepContextProps();
        for (Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String checkValue = stepContext.get(key);
            if (!StringUtils.equals(value, checkValue)) {
                throw new GmallException(OrderErrorCode.STEP_FORM_DATA_CHANGED);
            }
        }
    }

    protected void saveFormData(MainOrder mainOrder, Map<String, String> formData) {
        if (MapUtils.isEmpty(formData)) {
            return;
        }
        Map<String, String> props = mainOrder.getOrderAttr().getStepContextProps();
        if (props == null) {
            props = new HashMap<>();
            mainOrder.getOrderAttr().setStepContextProps(props);
        }
        props.putAll(formData);

        TcOrderDO up = new TcOrderDO();
        up.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        up.setOrderId(mainOrder.getPrimaryOrderId());
        up.setVersion(mainOrder.getVersion());
        up.setOrderAttr(mainOrder.getOrderAttr());
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        mainOrder.setVersion(up.getVersion());
    }

    protected void saveNextStep(MainOrder mainOrder, List<OrderChangeNotify> messageResult, OrderChangeOperateEnum op) {
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
        StepNext next = getNext(mainOrder);
        if (next != null) {
            // 推进next
            int nextNo = next.step.getStepNo();
            int nextStatus = next.op.getStatus();
            if (stepOrder.getStepNo() != nextNo) {
                updateStepStatus(stepOrder, StepOrderStatusEnum.STEP_FINISH.getCode());
                stepOrder = StepOrderUtils.getStepOrder(mainOrder, nextNo);
            }
            updateStepStatus(stepOrder, nextStatus);
            updateOrderStep(mainOrder, nextNo, nextStatus);
            createOpTimeoutTask(mainOrder, next);
            messageResult.add(OrderChangeNotify.builder()
                    .mainOrder(mainOrder).op(op).build());
        } else {
            // 订单完结
            StepMeta stepMeta = mainOrder.getStepTemplate().getStepMeta(stepOrder.getStepNo());
            Integer nextOrderStatus = stepMeta.getNextOrderStatus();
            updateStepStatus(stepOrder, StepOrderStatusEnum.STEP_FINISH.getCode());
            updateOrderStep(mainOrder, null, StepOrderStatusEnum.STEP_FINISH.getCode());
            if (nextOrderStatus == null) {
                // 订单完结
                OrderStatus finish = new OrderStatus();
                finish.setOrderStage(OrderStageEnum.AFTER_SALE.getCode());
                finish.setStatus(OrderStatusEnum.PAYMENT_CONFIRMED);
                finish.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMING);
                finish.setPrimaryOrderId(primaryOrderId);
                TradeBizResult<List<TcOrderDO>> res = orderStatusService.changeStepOrderStatus(finish);
                if (!res.isSuccess()) {
                    throw new GmallException(CommonErrorCode.SERVER_ERROR);
                }
                messageResult.add(OrderChangeNotify.builder()
                        .fromOrderStatus(OrderStatusEnum.PARTIALLY_PAID.getCode())
                        .orderList(res.getData()).op(OrderChangeOperateEnum.CUST_CONFIRM_RECEIVE).build());
            } else {
                // 订单到指定状态
                OrderStatusEnum t = OrderStatusEnum.codeOf(nextOrderStatus);
                OrderStatus targetStatus = new OrderStatus();
                targetStatus.setOrderStage(OrderStageEnum.ON_SALE.getCode());
                targetStatus.setStatus(t);
                targetStatus.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMING);
                targetStatus.setPrimaryOrderId(primaryOrderId);
                TradeBizResult<List<TcOrderDO>> res = orderStatusService.changeStepOrderStatus(targetStatus);
                if (!res.isSuccess()) {
                    throw new GmallException(CommonErrorCode.SERVER_ERROR);
                }
                if (t == OrderStatusEnum.ORDER_WAIT_DELIVERY) {
                    messageResult.add(OrderChangeNotify.builder()
                            .fromOrderStatus(OrderStatusEnum.PARTIALLY_PAID.getCode())
                            .orderList(res.getData()).op(OrderChangeOperateEnum.CUST_PAY).build());
                }
            }
        }
    }

    protected void createOpTimeoutTask(MainOrder mainOrder, StepNext next) {
        if (getNullZero(next.op.getTimeoutInSec()) <= 0
                || StringUtils.isBlank(next.op.getTimeoutAction())) {
            return;
        }

        StepTaskParam param = new StepTaskParam();
        param.setOp(next.op);
        param.setStep(next.step);
        param.setStepOrder(next.stepOrder);
        param.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        param.setSellerId(mainOrder.getSeller().getSellerId());
        param.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
        ScheduleTask task = stepOrderTimeoutTask.buildTask(param);
        orderTaskService.createScheduledTask(Collections.singletonList(task));
    }

    protected void updateOrderStep(MainOrder mainOrder, Integer stepNo, Integer stepStatus) {
        OrderAttrDO attr = mainOrder.getOrderAttr();
        StepOrder currentStepOrder =  mainOrder.getCurrentStepOrder();
        if (stepNo != null) {
            attr.setCurrentStepNo(stepNo);
        }
        if (stepStatus != null) {
            attr.setCurrentStepStatus(stepStatus);
        }

        TcOrderDO up = new TcOrderDO();
        up.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        up.setOrderId(mainOrder.getPrimaryOrderId());
        up.setVersion(mainOrder.getVersion());
        up.setOrderAttr(attr);
        if (Objects.nonNull(currentStepOrder) && Objects.nonNull(currentStepOrder.getFeatures())) {
            up.setPayCartId(currentStepOrder.getFeatures().getPayCartId());
        }
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        mainOrder.setVersion(up.getVersion());
    }

    protected void updateStepStatus(StepOrder stepOrder, int status) {
        TcStepOrderDO up = new TcStepOrderDO();
        up.setPrimaryOrderId(stepOrder.getPrimaryOrderId());
        up.setStepNo(stepOrder.getStepNo());
        up.setVersion(stepOrder.getVersion());
        up.setStatus(status);
        up.setFeatures(stepOrder.getFeatures());
        boolean success = tcStepOrderRepository.updateByUkVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        stepOrder.setStatus(status);
    }

    protected MainOrder updateOrderPayStatus(MainOrder mainOrder,
                                             List<OrderChangeNotify> messageResult) {
        List<TcOrderOperateFlowDO> list = Lists.newArrayList(OrderChangedNotifyServiceImpl.buildOrderPay(
                mainOrder, OrderStatusEnum.PAYMENT_CONFIRMING, OrderStatusEnum.PARTIALLY_PAID));
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        tcOrderRepository.updateStatusAndStageByPrimaryId(primaryOrderId,
                OrderStatusEnum.PARTIALLY_PAID.getCode(),
                OrderStageEnum.ON_SALE.getCode(), OrderStatusEnum.PAYMENT_CONFIRMING.getCode());
        mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        messageResult.add(OrderChangeNotify.builder()
                .mainOrder(mainOrder).flows(list).op(OrderChangeOperateEnum.CUST_PAY).build());
        return mainOrder;
    }

    protected StepNext getNext(MainOrder mainOrder) {
        Integer currentStep = mainOrder.orderAttr().getCurrentStepNo();
        StepOrder currentStepOrder = null;
        StepOrder nextStepOrder = null;
        for (StepOrder find : mainOrder.getStepOrders()) {
            if (find.getStepNo().intValue() == currentStep.intValue()) {
                currentStepOrder = find;
            } else if (currentStepOrder != null) {
                nextStepOrder = find;
                break;
            }
        }

        StepMeta currentStepTemplate = null;
        StepMeta nextStepTemplate = null;
        for (StepMeta find : mainOrder.getStepTemplate().getStepMetas()) {
            if (find.getStepNo().intValue() == currentStep.intValue()) {
                currentStepTemplate = find;
            } else if (currentStepTemplate != null) {
                nextStepTemplate = find;
                break;
            }
        }

        if (currentStepOrder == null || currentStepTemplate == null
                || currentStepOrder.getStepNo().intValue() != currentStepTemplate.getStepNo().intValue()
                || (nextStepOrder == null) != (nextStepTemplate == null)) {
            String msg = I18NMessageUtils.getMessage("order.exception")+":" + mainOrder.getPrimaryOrderId();  //# "订单异常
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, msg);
        }
        if (nextStepOrder != null && nextStepTemplate != null
                && nextStepOrder.getStepNo().intValue() != nextStepTemplate.getStepNo().intValue()) {
            String msg = I18NMessageUtils.getMessage("order.exception")+":" + mainOrder.getPrimaryOrderId();  //# "订单异常
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, msg);
        }

        List<StepNext> nextList = new ArrayList<>();
        nextList.add(new StepNext(currentStepTemplate, currentStepOrder, currentStepTemplate.getCustomerPay()));
        nextList.add(new StepNext(currentStepTemplate, currentStepOrder, currentStepTemplate.getSellerOperate()));
        nextList.add(new StepNext(currentStepTemplate, currentStepOrder, currentStepTemplate.getCustomerConfirm()));
        if (nextStepOrder != null) {
            nextList.add(new StepNext(nextStepTemplate, nextStepOrder, nextStepTemplate.getCustomerPay()));
            nextList.add(new StepNext(nextStepTemplate, nextStepOrder, nextStepTemplate.getSellerOperate()));
            nextList.add(new StepNext(nextStepTemplate, nextStepOrder, nextStepTemplate.getCustomerConfirm()));
        }

        boolean currentMath = false;
        for (StepNext next : nextList) {
            if (currentStepOrder.getStepNo().intValue() == next.stepOrder.getStepNo().intValue()
                    && currentStepOrder.getStatus().intValue() == next.op.getStatus().intValue()) {
                currentMath = true;
                continue;
            }
            if (currentMath && next.op.isNeed()) {
                return next;
            }
        }
        return null;
    }

    /*
    protected void savePaidAmt(MainOrder main) {
        PayAmt mainPaid = new PayAmt();
        for (StepOrder step : main.getStepOrders()) {
            StepOrderStatusEnum stepStatus = StepOrderStatusEnum.codeOf(step.getStatus());
            if (stepStatus != StepOrderStatusEnum.STEP_WAIT_PAY
                    && stepStatus != StepOrderStatusEnum.STEP_WAIT_START) {
                StepOrderPrice price = step.getPrice();
                mainPaid.setRealAmt(getNullZero(mainPaid.getRealAmt()) + getNullZero(price.getRealAmt()));
                mainPaid.setPointAmt(getNullZero(mainPaid.getPointAmt()) + getNullZero(price.getPointAmt()));
                mainPaid.setPointCount(getNullZero(mainPaid.getPointCount()) + getNullZero(price.getPointCount()));
            }
        }

        Divider realDiv = new Divider(
                getNullZero(mainPaid.getRealAmt()),
                getNullZero(main.getOrderPrice().getOrderRealAmt()),
                main.getSubOrders().size());
        Divider pointDiv = new Divider(
                getNullZero(mainPaid.getPointAmt()),
                getNullZero(main.getOrderPrice().getPointAmt()),
                main.getSubOrders().size());
        for (SubOrder subOrder : main.getSubOrders()) {
            Long subRealPaid = realDiv.next(getNullZero(subOrder.getOrderPrice().getOrderRealAmt()));
            Long subPointPaid = pointDiv.next(getNullZero(subOrder.getOrderPrice().getPointAmt()));
            Long subPointCount = null;
            if (getNullZero(subPointPaid) > 0L) {
                subPointCount = main.orderAttr().getPointExchange().exAmtToCount(subPointPaid);
            }

            PayAmt subPaid = new PayAmt();
            subPaid.setRealAmt(subRealPaid);
            subPaid.setPointAmt(subPointPaid);
            subPaid.setPointCount(subPointCount);

            OrderFeeAttrDO fee = orderConverter.toOrderFeeAttrDO(subOrder.getOrderPrice());
            fee.setPaidAmt(subPaid);

            TcOrderDO up = new TcOrderDO();
            up.setPrimaryOrderId(subOrder.getPrimaryOrderId());
            up.setOrderId(subOrder.getOrderId());
            up.setVersion(subOrder.getVersion());
            up.setOrderFeeAttr(fee);
            boolean success = tcOrderRepository.updateByOrderIdVersion(up);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            subOrder.setVersion(up.getVersion());
        }

        OrderFeeAttrDO mainFee = orderConverter.toOrderFeeAttrDO(main.getOrderPrice());
        mainFee.setPaidAmt(mainPaid);

        TcOrderDO up = new TcOrderDO();
        up.setPrimaryOrderId(main.getPrimaryOrderId());
        up.setOrderId(main.getPrimaryOrderId());
        up.setVersion(main.getVersion());
        up.setOrderFeeAttr(mainFee);
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        main.setVersion(up.getVersion());
    }
    */

    @AllArgsConstructor
    protected static class StepNext {
        protected StepMeta step;
        protected StepOrder stepOrder;
        protected BaseStepOperate op;
    }
}
