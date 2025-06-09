package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderTagPrefix;
import com.aliyun.gts.gmall.center.trade.common.constants.PresaleConstants;
import com.aliyun.gts.gmall.center.trade.common.util.DateUtils;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.OrderButtonsVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.BaseStepOperate;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.testng.collections.Maps;

import java.util.*;

/**
 * 订单操作工具类
 *
 * @author tiansong
 */
public class OrderUtils {

    public static List<String> getOrderStatusList(String tabCode) {
        OrderTabEnum orderTabEnum = OrderTabEnum.get(tabCode);
        if (OrderTabEnum.ALL.equals(orderTabEnum)) {
            return orderStatusTabMap.get(tabCode);
        }
        return orderStatusTabMap.get(tabCode);
    }

    /**
     * 订单列表TAB，与订单状态的映射关系
     */
    private static Map<String, List<String>> orderStatusTabMap = Maps.newConcurrentMap();

    static {
        String[] payment = {
            OrderStatusEnum.WAITING_FOR_PAYMENT.getCode().toString(),    // I18NMessageUtils.getMessage("pending.payment")  //# 待支付
            OrderStatusEnum.PAYMENT_CONFIRMING.getCode().toString(),
            OrderStatusEnum.PAYMENT_CONFIRMED.getCode().toString(),
            OrderStatusEnum.PARTIALLY_PAID.getCode().toString(),
            OrderStatusEnum.STEP_ORDER_DOING.getCode() + "_2_" + StepOrderStatusEnum.STEP_WAIT_PAY.getCode()  // 尾款待支付
        };
        String[] delivery = {
            OrderStatusEnum.DELIVERY_TO_DC.getCode().toString(),
            OrderStatusEnum.WAITING_FOR_COURIER.getCode().toString()
        };
        String[] receipt = {
            OrderStatusEnum.DELIVERY.getCode().toString(),
            OrderStatusEnum.READY_FOR_PICKUP.getCode().toString()
        };
        String[] evaluation = {OrderStatusEnum.COMPLETED.getCode().toString()};
        String[] reversing = {OrderStatusEnum.WAITING_FOR_ACCEPT.getCode().toString(),
                OrderStatusEnum.WAITING_FOR_RETURN.getCode().toString(),
                OrderStatusEnum.WAITING_FOR_REFUND.getCode().toString(),
                OrderStatusEnum.REFUND_REQUESTED.getCode().toString(),
                OrderStatusEnum.REFUND_APPROVED.getCode().toString(),
                OrderStatusEnum.REFUND_FULL_SUCCESS.getCode().toString(),
                OrderStatusEnum.REFUND_PART_SUCCESS.getCode().toString()
        };
        String[] all = {
            OrderStatusEnum.WAITING_FOR_ACCEPT.getCode().toString(),
            OrderStatusEnum.WAITING_FOR_RETURN.getCode().toString(),
            OrderStatusEnum.WAITING_FOR_REFUND.getCode().toString(),
            OrderStatusEnum.REFUND_REQUESTED.getCode().toString(),
            OrderStatusEnum.REFUND_APPROVED.getCode().toString(),
            OrderStatusEnum.REFUND_FULL_SUCCESS.getCode().toString(),
            OrderStatusEnum.REFUND_PART_SUCCESS.getCode().toString(),
            OrderStatusEnum.COMPLETED.getCode().toString(),
            OrderStatusEnum.DELIVERY.getCode().toString(),
            OrderStatusEnum.READY_FOR_PICKUP.getCode().toString(),
            OrderStatusEnum.DELIVERY_TO_DC.getCode().toString(),
            OrderStatusEnum.WAITING_FOR_COURIER.getCode().toString(),
            OrderStatusEnum.WAITING_FOR_PAYMENT.getCode().toString(),
            OrderStatusEnum.PAYMENT_CONFIRMED.getCode().toString(),
            OrderStatusEnum.PARTIALLY_PAID.getCode().toString(),
            OrderStatusEnum.CANCEL_REQUESTED.getCode().toString(),
            OrderStatusEnum.CANCELLED.getCode().toString(),
            OrderStatusEnum.CANCEL_FAILED.getCode().toString(),
            OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode().toString(),
            OrderStatusEnum.RETURNING_TO_MERCHANT.getCode().toString(),
            OrderStatusEnum.REFUND_FAILED.getCode().toString()
        };

        orderStatusTabMap.put(OrderTabEnum.PENDING_PAYMENT.getCode(), Arrays.asList(payment));
        orderStatusTabMap.put(OrderTabEnum.PENDING_DELIVERY.getCode(), Arrays.asList(delivery));
        orderStatusTabMap.put(OrderTabEnum.PENDING_RECEIPT.getCode(), Arrays.asList(receipt));
        orderStatusTabMap.put(OrderTabEnum.PENDING_EVALUATION.getCode(), Arrays.asList(evaluation));
        orderStatusTabMap.put(OrderTabEnum.REVERSING.getCode(), Arrays.asList(reversing));
        orderStatusTabMap.put(OrderTabEnum.ALL.getCode(), Arrays.asList(all));

    }

    public static OrderButtonsVO getButtonsByStatus(Integer orderStatus) {
        OrderButtonsVO orderButtonsVO = orderButtonsVOMap.get(orderStatus);
        if (orderButtonsVO == null) {
            return OrderButtonsVO.builder().deleted(true).build();
        }
        // 需要将常量copy，便于后续根据其他状态修改订单按钮
        OrderButtonsVO newButtons = OrderButtonsVO.builder().build();
        BeanUtils.copyProperties(orderButtonsVO, newButtons);
        return newButtons;
    }

    /**
     * 订单状态，与显示按钮的关系
     */
    private static Map<Integer, OrderButtonsVO> orderButtonsVOMap = Maps.newConcurrentMap();

    static {
        // 待付款
        orderButtonsVOMap.put(OrderStatusEnum.ORDER_WAIT_PAY.getCode(), OrderButtonsVO.builder().pay(true).cancel(true)
            .build());
        //等待卖家确认接单
        //orderButtonsVOMap.put(OrderStatusEnum.WAIT_SELLER_CONFIRM.getCode(), OrderButtonsVO.builder().cancel(true)
        //    .build());
        // 待发货
        OrderButtonsVO emptyButton = OrderButtonsVO.builder().build();
        orderButtonsVOMap.put(OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode(), emptyButton);
        // REVERSAL_DOING(40, "售后进行中");
        orderButtonsVOMap.put(OrderStatusEnum.REVERSAL_DOING.getCode(), emptyButton);
        // 已发货
        orderButtonsVOMap.put(OrderStatusEnum.ORDER_SENDED.getCode(), OrderButtonsVO.builder().logistic(true)
            .receipt(true).build());
        // 确认收货/系统确认收货/交易成功
        OrderButtonsVO orderButtonsVO = OrderButtonsVO.builder().evaluation(true).logistic(true).build();
        orderButtonsVOMap.put(OrderStatusEnum.ORDER_CONFIRM.getCode(), orderButtonsVO);
        orderButtonsVOMap.put(OrderStatusEnum.SYSTEM_CONFIRM.getCode(), orderButtonsVO);
        // 其他状态，默认只有删除按钮了
    }

    private static Integer[] waitReversal = {ReversalStatusEnum.WAIT_SELLER_AGREE.getCode(),
        ReversalStatusEnum.WAIT_DELIVERY.getCode(), ReversalStatusEnum.WAIT_CONFIRM_RECEIVE.getCode(),
        ReversalStatusEnum.WAIT_REFUND.getCode()};

    /**
     * 获取售后进行中状态
     *
     * @return
     */
    public static List<Integer> getReversalWaitStatus() {
        return Arrays.asList(waitReversal);
    }

    public static OrderButtonsVO getOrderButtons(MainOrderDTO mainOrder) {
        OrderButtonsVO orderButtonsVO = getButtonsByStatus(mainOrder.getOrderStatus());
        // 是否显示更多按钮
        if (OrderStatusEnum.ORDER_CONFIRM.getCode().equals(mainOrder.getOrderStatus()) ||
                OrderStatusEnum.SYSTEM_CONFIRM.getCode().equals(mainOrder.getOrderStatus()) ||
                ((OrderStatusEnum.REVERSAL_SUCCESS.getCode().equals(mainOrder.getOrderStatus()) ||
                        OrderStatusEnum.REVERSAL_DOING.getCode().equals(mainOrder.getOrderStatus())) &&
                        Objects.nonNull(mainOrder.getExtra()) &&
                        !mainOrder.getExtra().containsKey(BizConst.INVOICE_ID) &&
                        mainOrder.getSubOrderList().stream().filter(item ->
                                !item.getFeature().containsKey(BizConst.REVERSAL_ORDER_STATUS)).count() > 0)) {
            //确认收货，或者只进行部分售后且从未进行申请
            orderButtonsVO.setOther(true);
        }

        // 评价处理
        if (mainOrder.getEvaluate() != null && orderButtonsVO.getEvaluation() != null) {
            orderButtonsVO.setEvaluation(!OrderEvaluateEnum.isEvaluated(mainOrder.getEvaluate()));
            orderButtonsVO.setAdditionalEvaluation(OrderEvaluateEnum.FIRST_EVALUATED.getCode().equals(mainOrder.getEvaluate()));
        }
        // 电子凭证，无物流，确认收货变更为待核销
        if (ExtOrderType.EVOUCHER.getCode().equals(mainOrder.getOrderType())) {
            orderButtonsVO.setVerification(orderButtonsVO.getReceipt());
            orderButtonsVO.setReceipt(null);
            orderButtonsVO.setLogistic(null);
        }
        // 多阶段
        if (OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(mainOrder.getOrderType())
                && OrderStatusEnum.STEP_ORDER_DOING.getCode().equals(mainOrder.getOrderStatus())) {
            orderButtonsVO.setDeleted(false);
            StepOrderDTO stepOrder = mainOrder.getCurrentStepOrder();
            if (stepOrder != null) {
                if (StepOrderStatusEnum.STEP_WAIT_PAY.getCode().equals(stepOrder.getStatus())) {
                    orderButtonsVO.setPay(true);
                    if (isPreSaleNotPay(mainOrder)) {  // 预售非支付时间
                        orderButtonsVO.setPay(false);
                    }
                }
                if (StepOrderStatusEnum.STEP_WAIT_CONFIRM.getCode().equals(stepOrder.getStatus())) {
                    orderButtonsVO.setConfirmStepOrder(true);
                }
            }
        }
        // 赠品
        if (isGift(mainOrder)) {
            orderButtonsVO.setPay(null);
            orderButtonsVO.setCancel(null);
        }
        return orderButtonsVO;
    }

    // 展示标签
    public static List<String> getShowTags(SubOrderDTO subOrder) {
        if (isGift(subOrder)) {
            return Arrays.asList(I18NMessageUtils.getMessage("gift"));  //# "赠品"
        }
        return null;
    }

    private static boolean isGift(MainOrderDTO mainOrder) {
        boolean allGift = true;
        for (SubOrderDTO subOrder : mainOrder.getSubOrderList()) {
            if (!isGift(subOrder)) {
                allGift = false;
                break;
            }
        }
        return allGift;
    }

    private static boolean isGift(SubOrderDTO subOrder) {
        return subOrder.getTags() != null && subOrder.getTags().contains(OrderTagPrefix.MANZENG_GIFT);
    }

    private static boolean isPreSaleNotPay(MainOrderDTO mainOrder) {
        // 非预售
        if (mainOrder.getBizCodes() == null || !mainOrder.getBizCodes().contains(ExtBizCode.PRE_SALE)) {
            return false;
        }
        // 非尾款
        if (mainOrder.getCurrentStepNo() == null || mainOrder.getCurrentStepNo().intValue() != 2) {
            return false;
        }

        Map<String, String> map = mainOrder.getStepContextProps();
        String tailStart = map.get(PresaleConstants.CONTEXT_TAIL_START);
        String tailEnd = map.get(PresaleConstants.CONTEXT_TAIL_END);
        long now = System.currentTimeMillis();
        if (StringUtils.isNotBlank(tailStart)) {
            Date startDate = DateUtils.parseDateTime(tailStart);
            if (startDate.getTime() > now) {
                return true;
            }
        }
        if (StringUtils.isNotBlank(tailEnd)) {
            Date endDate = DateUtils.parseDateTime(tailEnd);
            if (endDate.getTime() < now) {
                return true;
            }
        }
        return false;
    }

    public static SubOrderButton getSubOrderButton(MainOrderDTO mainOrder, SubOrderDTO subOrder, ReversalSubOrderDTO reversal) {
        SubOrderButton b = new SubOrderButton();

        // === 加购按钮 ===

        b.showAddCart = true;
        if (ExtOrderType.EVOUCHER.getCode().equals(mainOrder.getOrderType())
                || OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(mainOrder.getOrderType())) {
            b.showAddCart = false;
        }

        // === 售后按钮 ===
        if(reversal == null){
            return b;
        }
        if (!reversal.isAllowReversal() || reversal.getMaxCancelQty() == 0 || reversal.getMaxCancelAmt() == 0L) {
           return b;
        }
        Integer subStatus = subOrder.getOrderStatus();
        if (OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().equals(subStatus)) {
            b.showReversal = true;
            b.showReversalRefund = true;
            b.showReversalReturnItem = false;
            return b;
        }
        if (OrderStatusEnum.ORDER_SENDED.getCode().equals(subStatus)
                || OrderStatusEnum.ORDER_CONFIRM.getCode().equals(subStatus)
                || OrderStatusEnum.SYSTEM_CONFIRM.getCode().equals(subStatus)
                || OrderStatusEnum.REVERSAL_SUCCESS.getCode().equals(subStatus)) {
            b.showReversal = true;
            b.showReversalRefund = true;
            b.showReversalReturnItem = true;
            return b;
        }
        if (OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(mainOrder.getOrderType())) {
            StepOrderDTO stepOrder = mainOrder.getCurrentStepOrder();
            if (stepOrder != null) {
                StepMeta stepMeta = mainOrder.getStepTemplate().getStepMeta(stepOrder.getStepNo());
                BaseStepOperate op = stepMeta.getOperate(stepOrder.getStatus());
                if(Objects.nonNull(op)) {
                    List<String> other = op.getOtherOperation();
                    if (other != null && other.contains(BaseStepOperate.OPERATION_REVERSAL_REFUND_ONLY)) {
                        b.showReversal = true;
                        b.showReversalRefund = true;
                    }
                    if (other != null && other.contains(BaseStepOperate.OPERATION_REVERSAL_RETURN_ITEM)) {
                        b.showReversal = true;
                        b.showReversalReturnItem = true;
                    }
                }
            }
        }

        return b;
    }

    @Getter
    public static class SubOrderButton {
        boolean showReversal;
        boolean showReversalRefund;
        boolean showReversalReturnItem;
        boolean showAddCart;

        boolean showApplyCancel;
    }
}