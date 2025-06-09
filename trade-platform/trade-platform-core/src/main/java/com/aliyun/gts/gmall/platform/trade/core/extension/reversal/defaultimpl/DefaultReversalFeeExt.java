package com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.ReversalErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.FifoDivider;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepRefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubRefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils.getNullZero;

@Slf4j
@Component
public class DefaultReversalFeeExt implements ReversalFeeExt {
    @Autowired
    private ReversalQueryService reversalQueryService;

    @Value("${trade.point.allowFullDeduct:true}")
    private Boolean allowFullDeduct;

    /**
     * 输入：指定主售后单、子售后单中的退款金额 (cancelAmt)，也可以不指定，退还默认金额
     * 输出： 填充主售后单、子售后单中的 cancelAmt、pointAmt、pointCount、realAmt、cancelSeparateAmt
     *      fixbug: 同时校验&填充 cancelQty
     *      202305: 运费独立到 mainReversal.reversalFeatures.cancelFreightFee
     *
     * 逻辑：校验+分摊
     * 默认实现:
     * 1：指定的子单金额、件数   -- 按指定数退
     * 2：不指定的子单金额、件数  -- 全退
     * 3：主单上的退回金额、件数忽略
     * 确认 售后不退运费 20250322
     */
    @Override
    public void divideCancelAmt(MainReversal reversal) {
        List<SubRefundFee> allSubFee = reversal.getAllSubFee();
        // ========== 剩余最大可退额度 ==========
        RefundCalc mainRemain = new RefundCalc();
        List<MainReversal> historyList = null;
        Map<Long, RefundCalc> subRemains = new HashMap<>();
        {
            RefundInnerCalc zero = new RefundInnerCalc();
            // 根据主单查询所有的退单记录
            historyList = reversalQueryService.queryReversalByOrder(reversal.getMainOrder().getPrimaryOrderId());
            // 过滤掉失效的退单记录
            historyList = filterHistoryReversal(historyList);
            // 是否退运费  售后不退运费20250322
            boolean returnFreight = canReturnFreight(reversal, historyList);
            // 所有退单子单对象
            for (SubRefundFee sub : allSubFee) {
                RefundCalc subRemain;
                // 不可退运费
                subRemain = new RefundCalc();
                if (sub.isFreight() && !returnFreight) {
                    subRemain = new RefundCalc();
                } else {
                    RefundCalc subHistory = getHistoryAmt(historyList, sub.getSubOrderId());
                    RefundCalc subTotal = getTotalAmt(sub, reversal.getMainOrder());
                    subRemain = subTotal.copy();
                    subRemain.sub(subHistory);
                    if (zero.anyGt(subRemain.total) ||
                        subRemain.cancelQty < 0) {
                        throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_OUT_OF_FEE);
                    }
                }
                mainRemain.add(subRemain);
                subRemains.put(sub.getSubOrderId(), subRemain);
            }
        }
        // ========== 本次退回额度 ==========
        RefundCalc mainRefund = new RefundCalc();
        Map<Long, RefundCalc> subRefunds = new HashMap<>();
        for (SubRefundFee sub : allSubFee) {
            RefundCalc subRefund = new RefundCalc();
            RefundCalc subRemain = subRemains.get(sub.getSubOrderId());
            subRefunds.put(sub.getSubOrderId(), subRefund);
            // ========== 退回件数 ==========
            if (sub.getCancelQty() == null) {
                subRefund.cancelQty = subRemain.cancelQty;
            } else {
                if (sub.getCancelQty().intValue() < 0 || sub.getCancelQty().intValue() > subRemain.cancelQty) {
                    throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_OUT_OF_QTY);
                }
                subRefund.cancelQty = sub.getCancelQty();
            }
            // ========== 退回金额 ==========
            if (sub.getCancelAmt() == null || sub.getCancelAmt().longValue() == subRemain.total.getTotalAmt()) {
                subRefund.total = subRemain.total;
                subRefund.stepMap = subRemain.stepMap;
            } else {
                if (sub.getCancelAmt().intValue() < 0 || sub.getCancelAmt().intValue() > subRemain.total.getTotalAmt()) {
                    throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_OUT_OF_FEE);
                }
                PointExchange ex = reversal.getMainOrder().orderAttr().getPointExchange();
                RefundCalc d = divideRefund(subRemain, sub.getCancelAmt(), ex);
                subRefund.total = d.total;
                subRefund.stepMap = d.stepMap;
            }
            // 主单求和
            mainRefund.add(subRefund);
        }
        // 主单校验一下
        if (mainRefund.anyGt(mainRemain)) {
            throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_DIVIDE_FEE_ERROR);
        }
        // ========== 回填 ==========
        reversal.setCancelQty(mainRefund.cancelQty);
        reversal.setCancelAmt(mainRefund.total.getTotalAmt());
        fillResult(reversal.reversalFeatures(), mainRefund);
        for (SubRefundFee sub : allSubFee) {
            RefundCalc subRefund = subRefunds.get(sub.getSubOrderId());
            sub.setCancelQty(subRefund.cancelQty);
            sub.setCancelAmt(subRefund.total.getTotalAmt());
            fillResult(sub.getRefundFee(), subRefund);
        }
    }


    /**
     * 本次是否可退运费  应该读取新的配置表 这个逻辑不对！！
     * @param reversal
     * @param historyList
     * @return
     */
    protected boolean canReturnFreight(MainReversal reversal, List<MainReversal> historyList) {
        // 所有子单都退了(含部分金额), 则可退运费
        Set<Long> subOrderIds = reversal.getMainOrder()
            .getSubOrders()
            .stream()
            .map(SubOrder::getOrderId)
            .collect(Collectors.toSet());
        historyList.stream()
            .map(MainReversal::getSubReversals)
            .flatMap(List::stream)
            .filter(sr -> NumUtils.getNullZero(sr.getCancelAmt()) > 0L)
            .map(SubReversal::getSubOrder)
            .map(SubOrder::getOrderId)
            .forEach(subOrderIds::remove);
        reversal.getSubReversals()
            .stream()
            .map(SubReversal::getSubOrder)
            .map(SubOrder::getOrderId)
            .forEach(subOrderIds::remove);
        return subOrderIds.isEmpty();
    }

    /**
     * 过滤有效的退款记录;排除失效的
     * @param history
     * @return
     */
    protected List<MainReversal> filterHistoryReversal(List<MainReversal> history) {
        if (CollectionUtils.isEmpty(history)) {
            return history;
        }
        return history.stream().filter(this::isValid).collect(Collectors.toList());
    }

    protected void fillResult(StepRefundFee target, RefundCalc result) {
        fillResult(target, result.total);
        Map<Integer, RefundFee> map = new HashMap<>();
        for (Entry<Integer, RefundInnerCalc> en : result.stepMap.entrySet()) {
            RefundFee value = new RefundFee();
            fillResult(value, en.getValue());
            map.put(en.getKey(), value);
        }
        target.setStepRefundFee(map);
    }

    protected void fillResult(RefundFee target, RefundInnerCalc result) {
        target.setCancelPointAmt(result.pointAmt);
        target.setCancelPointCount(result.pointCount);
        target.setCancelRealAmt(result.realAmt);
        target.setCancelSeparatePointAmt(result.separatePointAmt);
        target.setCancelSeparateRealAmt(result.separateRealAmt);
    }

    // 总金额分摊 => 积分、现金、分账
    protected RefundCalc divideRefund(RefundCalc remain, long targetAmt, PointExchange ex) {
        RefundCalc copy = remain.copy();
        if (MapUtils.isNotEmpty(copy.stepMap)) {
            RefundInnerCalc sum = new RefundInnerCalc();
            // 每个阶段退多少, 不采用平均分摊, 优先退前面阶段
            // Divider d = new Divider(targetAmt, remain.total.getTotalAmt(), remain.stepMap.size());
            FifoDivider d = new FifoDivider(targetAmt);
            for (Entry<Integer, RefundInnerCalc> step : copy.stepMap.entrySet()) {
                RefundInnerCalc stepFee = step.getValue();
                long stepTargetAmt = d.next(stepFee.getTotalAmt());
                divideInnerRefund(stepFee, stepTargetAmt, ex);
                sum.add(stepFee);
            }
            copy.total = sum;
        } else {
            divideInnerRefund(copy.total, targetAmt, ex);
        }
        return copy;
    }

    protected void divideInnerRefund(RefundInnerCalc refund, long targetAmt, PointExchange ex) {
        List<Long> stepDivide = DivideUtils.divide(targetAmt, Arrays.asList(refund.pointAmt, refund.realAmt));
        refund.pointAmt = stepDivide.get(0);
        refund.realAmt = stepDivide.get(1);
        if (ex != null && refund.pointAmt > 0) {
            refund.pointCount = ex.exAmtToCount(refund.pointAmt);
        } else {
            refund.pointCount = 0;
        }

        // 分账金额分摊 -- 积分
        refund.separatePointAmt = divideSeparateAmt(refund.separatePointAmt, refund.pointAmt);
        // 分账金额分摊 -- 现金
        refund.separateRealAmt = divideSeparateAmt(refund.separateRealAmt, refund.realAmt);
    }

    protected Map<String, Long> divideSeparateAmt(Map<String, Long> separateMap, long targetAmt) {
        long separateSum = separateMap.values().stream().mapToLong(Long::longValue).sum();
        if (separateSum <= 0) {
            return separateMap;
        }

        Map<String, Long> map = new HashMap<>();
        Divider div = new Divider(targetAmt, separateSum, separateMap.size());
        for (Entry<String, Long> roleAmt : separateMap.entrySet()) {
            String role = roleAmt.getKey();
            long roleTarget = div.next(getNullZero(roleAmt.getValue()));
            map.put(role, roleTarget);
        }
        return map;
    }

    /**
     * 业务限制校验
     */
    protected void bizCheckAfterDivide(RefundCalc subRefund, RefundCalc subRemain,
                                       SubRefundFee subReversal, MainReversal mainReversal) {
        // 判断 未发货
        boolean waitDelivery;
        if (subReversal.isSubReversal()) {
            Integer ordStatus = subReversal.getRevSubOrder().getOrderStatus();
            waitDelivery = OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().equals(ordStatus);
        } else {
            waitDelivery = mainReversal.getSubReversals().stream()
                    .map(SubReversal::getSubOrder).map(SubOrder::getOrderStatus)
                    .allMatch(s -> OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().equals(s));
        }

        // 待发货规则: 仅支持全额退款, 不支持部分退款、退货退款
        if (waitDelivery && !ReversalTypeEnum.APPLY_CANCEL_REFUND.getCode().equals(mainReversal.getReversalType())) {
            if (subRefund.total.getTotalAmt() != subRemain.total.getTotalAmt()) {
                throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_REFUND_ALL_ONLY);
            }
            ReversalTypeEnum type = ReversalTypeEnum.codeOf(mainReversal.getReversalType());
            if (type != null && !(type == ReversalTypeEnum.REFUND_ONLY || type == ReversalTypeEnum.APPLY_CANCEL_REFUND || type == ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND)) {
                throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_REFUND_ALL_ONLY);
            }
        }

        // 不允许全积分的情况
        if (!allowFullDeduct.booleanValue()
                && subRemain.total.realAmt - subRefund.total.realAmt == 0
                && subRemain.total.pointAmt - subRefund.total.pointAmt > 0) {
            throw new GmallException(ReversalErrorCode.CREATE_REVERSAL_DIVIDE_FEE_ERROR);
        }
    }

    // 子单总额, 不含未支付
    protected RefundCalc getTotalAmt(SubRefundFee subOrder, MainOrder mainOrder) {
        RefundCalc calc = new RefundCalc();
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            RefundInnerCalc sum = new RefundInnerCalc();
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                if (!isPaid(stepOrder)) {
                    continue;
                }
                StepOrderPrice subStepPrice = subOrder.getOrderPrice().getStepAmt().get(stepOrder.getStepNo());
                RefundInnerCalc inner = toRefundInnerCalc(subStepPrice);
                calc.stepMap.put(stepOrder.getStepNo(), inner);
                sum.add(inner);
            }
            calc.total = sum;
        } else {
            calc.total = toRefundInnerCalc(subOrder.getOrderPrice());
        }
        calc.cancelQty = subOrder.getOrderQty();
        return calc;
    }

    protected boolean isPaid(StepOrder stepOrder) {
        StepOrderStatusEnum status = StepOrderStatusEnum.codeOf(stepOrder.getStatus());
        return status != StepOrderStatusEnum.STEP_WAIT_PAY && status != StepOrderStatusEnum.STEP_WAIT_START;
    }

    // 子单历史已退额
    protected RefundCalc getHistoryAmt(List<MainReversal> history, Long subOrderId) {
        RefundCalc sum = new RefundCalc();
        if (CollectionUtils.isEmpty(history)) {
            return sum;
        }
        for (MainReversal main : history) {
            for (SubRefundFee sub : main.getAllSubFee()) {
                long subId = sub.getSubOrderId();
                if (subId == subOrderId) {
                    RefundCalc subAmt = toRefundCalc(sub.getRefundFee());
                    if (isReturnItem(main)) {
                        subAmt.cancelQty = getNullZero(sub.getCancelQty());
                    }
                    sum.add(subAmt);
                    break;
                }
            }
        }
        return sum;
    }

    // 取消的、拒绝的售后单
    protected boolean isValid(MainReversal his) {
        ReversalStatusEnum status = ReversalStatusEnum.codeOf(his.getReversalStatus());
        if( status == ReversalStatusEnum.REFUND_FAILED ){
            return false;
        }
        return status != ReversalStatusEnum.CANCELLED;
    }

    // 是否含退货
    protected boolean isReturnItem(MainReversal his) {
        ReversalTypeEnum type = ReversalTypeEnum.codeOf(his.getReversalType());
        return type == ReversalTypeEnum.REFUND_ITEM;
    }


    // ============== 内部计算模型 ==============

    @Data
    protected static class RefundCalc {
        public RefundInnerCalc total = new RefundInnerCalc();
        public Map<Integer, RefundInnerCalc> stepMap = new HashMap<>();
        public int cancelQty;

        public void add(RefundCalc other) {
            total.add(other.total);
            cancelQty += other.cancelQty;
            stepMap = CommUtils.merge(stepMap, other.stepMap, (v1, v2) -> {
                RefundInnerCalc copy = v1 == null ? new RefundInnerCalc() : v1.copy();
                if (v2 != null) {
                    copy.add(v2);
                }
                return copy;
            });
        }

        public void sub(RefundCalc other) {
            total.sub(other.total);
            cancelQty -= other.cancelQty;
            stepMap = CommUtils.merge(stepMap, other.stepMap, (v1, v2) -> {
                RefundInnerCalc copy = v1 == null ? new RefundInnerCalc() : v1.copy();
                if (v2 != null) {
                    copy.sub(v2);
                }
                return copy;
            });
        }

        public RefundCalc copy() {
            RefundCalc c = new RefundCalc();
            c.total = total.copy();
            c.cancelQty = cancelQty;
            for (Entry<Integer, RefundInnerCalc> en : stepMap.entrySet()) {
                c.stepMap.put(en.getKey(), en.getValue().copy());
            }
            return c;
        }

        public boolean anyGt(RefundCalc other) {
            if (cancelQty > other.cancelQty || total.anyGt(other.total)) {
                return true;
            }
            Map<Integer, RefundInnerCalc> m1 = stepMap;
            Map<Integer, RefundInnerCalc> m2 = other.stepMap;
            Set<Integer> allKeys = new HashSet<>();
            allKeys.addAll(m1.keySet());
            allKeys.addAll(m2.keySet());
            for (Integer key : allKeys) {
                RefundInnerCalc curValue = m1.get(key);
                RefundInnerCalc otherValue = m2.get(key);
                if (curValue == null) {
                    continue;
                }
                if (otherValue == null || curValue.anyGt(otherValue)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Data
    protected static class RefundInnerCalc {
        public long pointAmt;
        public long pointCount;
        public long realAmt;
        public Map<String, Long> separateRealAmt = new HashMap<>();
        public Map<String, Long> separatePointAmt = new HashMap<>();

        public long getTotalAmt() {
            return pointAmt + realAmt;
        }

        // 加法
        public void add(RefundInnerCalc other) {
            pointAmt += other.pointAmt;
            pointCount += other.pointCount;
            realAmt += other.realAmt;
            separateRealAmt = CommUtils.addMergeLong(separateRealAmt, other.separateRealAmt);
            separatePointAmt = CommUtils.addMergeLong(separatePointAmt, other.separatePointAmt);
        }

        // 减法
        public void sub(RefundInnerCalc other) {
            pointAmt -= other.pointAmt;
            pointCount -= other.pointCount;
            realAmt -= other.realAmt;
            separateRealAmt = CommUtils.subMergeLong(separateRealAmt, other.separateRealAmt);
            separatePointAmt = CommUtils.subMergeLong(separatePointAmt, other.separatePointAmt);
        }

        // 克隆
        public RefundInnerCalc copy() {
            RefundInnerCalc c = new RefundInnerCalc();
            c.pointAmt = pointAmt;
            c.pointCount = pointCount;
            c.realAmt = realAmt;
            c.separateRealAmt = new HashMap<>(separateRealAmt);
            c.separatePointAmt = new HashMap<>(separatePointAmt);
            return c;
        }

        // 大于
        public boolean anyGt(RefundInnerCalc other) {
            if (pointAmt > other.pointAmt
                    || pointCount > other.pointCount
                    || realAmt > other.realAmt) {
                return true;
            }
            if (anyGt(separateRealAmt, other.separateRealAmt)) {
                return true;
            }
            return anyGt(separatePointAmt, other.separatePointAmt);
        }

        public static boolean anyGt(Map<String, Long> m1, Map<String, Long> m2) {
            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(m1.keySet());
            allKeys.addAll(m2.keySet());
            for (String key : allKeys) {
                long curValue = getNullZero(m1.get(key));
                long otherValue = getNullZero(m2.get(key));
                if (curValue > otherValue) {
                    return true;
                }
            }
            return false;
        }
    }

    protected RefundCalc toRefundCalc(StepRefundFee fee) {
        RefundCalc c = new RefundCalc();
        c.total = toRefundInnerCalc(fee);
        if (fee.getStepRefundFee() != null) {
            c.stepMap = new HashMap<>();
            for (Entry<Integer, RefundFee> en : fee.getStepRefundFee().entrySet()) {
                c.stepMap.put(en.getKey(), toRefundInnerCalc(en.getValue()));
            }
        }
        return c;
    }

    protected RefundInnerCalc toRefundInnerCalc(RefundFee fee) {
        RefundInnerCalc c = new RefundInnerCalc();
        c.pointAmt = getNullZero(fee.getCancelPointAmt());
        c.pointCount = getNullZero(fee.getCancelPointCount());
        c.realAmt = getNullZero(fee.getCancelRealAmt());
        c.separatePointAmt = fee.getCancelSeparatePointAmt();
        c.separateRealAmt = fee.getCancelSeparateRealAmt();
        return c;
    }

    protected RefundInnerCalc toRefundInnerCalc(StepOrderPrice fee) {
        RefundInnerCalc c = new RefundInnerCalc();
        c.pointAmt = getNullZero(fee.getPointAmt());
        c.pointCount = getNullZero(fee.getPointCount());
        c.realAmt = getNullZero(fee.getRealAmt());
        if (fee.getConfirmPrice() != null) {
            c.separatePointAmt = fee.getConfirmPrice().getSeparatePointAmt();
            c.separateRealAmt = fee.getConfirmPrice().getSeparateRealAmt();
        }
        return c;
    }

    protected RefundInnerCalc toRefundInnerCalc(OrderPrice fee) {
        RefundInnerCalc c = new RefundInnerCalc();
        c.pointAmt = getNullZero(fee.getPointAmt());
        c.pointCount = getNullZero(fee.getPointCount());
        c.realAmt = getNullZero(fee.getOrderRealAmt());
        if (fee.getConfirmPrice() != null) {
            c.separatePointAmt = fee.getConfirmPrice().getSeparatePointAmt();
            c.separateRealAmt = fee.getConfirmPrice().getSeparateRealAmt();
        }
        return c;
    }
}
