package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderSeparateFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepRefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemCategory;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ConfirmPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.SeparateRule;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import static com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils.getNullZero;

@Slf4j
@Component
public class DefaultOrderSeparateFeeExt implements OrderSeparateFeeExt {
    private static final String SEP_KEY = "charge_percent";
    private static final int SEP_FIX = 100;
    private static final int SEP_MAX = 10000;

    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Value("${trade.separate.floorRole:platform}")
    private String floorRole;

    @Value("${trade.separate.defaultRole:seller}")
    private String defaultRole;

    @Override
    public void storeSeparateRule(MainOrder mainOrder) {
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            ItemCategory cate = subOrder.getItemSku().getItemCategory();
            if (cate == null) {
                continue;
            }
            String value = cate.getFeatureMap().get(SEP_KEY);
            if (StringUtils.isBlank(value)) {
                continue;
            }
            double d;
            try {
                d = Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                continue;
            }
            int commissionValue = (int) Math.round(d * SEP_FIX);
            if (commissionValue <= 0 || commissionValue >= SEP_MAX) {
                continue;
            }

            int sellerValue = SEP_MAX - commissionValue;
            int platformValue = commissionValue;
            Map<String, Integer> roles = new HashMap<>();
            roles.put(SeparateRule.ROLE_SELLER, sellerValue);
            roles.put(SeparateRule.ROLE_PLATFORM, platformValue);
            SeparateRule rule = new SeparateRule();
            rule.setRoles(roles);
            subOrder.orderAttr().setSeparateRule(rule);
        }
    }

    @Override
    public void storeConfirmPrice(MainOrder mainOrder) {
        // 计算实付款
        calcConfirmAmt(mainOrder);
        // 计算分账
        calcSeparateAmt(mainOrder);
    }

    // 计算实付款
    protected void calcConfirmAmt(MainOrder mainOrder) {
        // 售中退款金额
        Map<Long, PayCalc> cancelMap = new HashMap<>();
        List<MainReversal> reList = reversalQueryService.queryReversalByOrder(mainOrder.getPrimaryOrderId());
        for (MainReversal re : reList) {
            ReversalStatusEnum status = ReversalStatusEnum.codeOf(re.getReversalStatus());
            OrderStageEnum stage = OrderStageEnum.codeOf(re.getReversalFeatures().getOrderStage());

            // 仅保留 成功的、 售中的
            if (status != ReversalStatusEnum.REVERSAL_OK || stage != OrderStageEnum.ON_SALE) {
                continue;
            }

            PayCalc mainCancel = CommUtils.getValue(cancelMap, re.getMainOrder().getPrimaryOrderId(), PayCalc::new);
            PayCalc mainAmt = PayCalc.from(re.getReversalFeatures());
            mainCancel.add(mainAmt);

            // 子订单
            for (SubReversal sr : re.getSubReversals()) {
                PayCalc subCancel = CommUtils.getValue(cancelMap, sr.getSubOrder().getOrderId(), PayCalc::new);
                PayCalc subAmt = PayCalc.from(sr.getReversalFeatures());
                subCancel.add(subAmt);
            }

            // 运费子单
            Long fid = OrderIdUtils.getFreightOrderId(re.getMainOrder().getPrimaryOrderId());
            PayCalc fCancel = CommUtils.getValue(cancelMap, fid, PayCalc::new);
            StepRefundFee fFee = re.getReversalFeatures().getCancelFreightFee();
            if (fFee != null) {
                fCancel.add(PayCalc.from(fFee));
            }
        }

        // 已支付金额, 多阶段的情况下 已支付金额 != 订单金额
        Map<Long, PayCalc> paidMap = new HashMap<>();
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            PayCalc mainPaid = new PayCalc();
            paidMap.put(mainOrder.getPrimaryOrderId(), mainPaid);
            for (SubPrice subPrice : mainOrder.getAllSubPrice()) {
                PayCalc subPaid = new PayCalc();
                paidMap.put(subPrice.getSubOrderId(), subPaid);
                for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                    if (!StepOrderUtils.isPaid(stepOrder)) {
                        continue;
                    }
                    int stepNo = stepOrder.getStepNo();
                    StepOrderPrice stepPrice = subPrice.getOrderPrice().getStepAmt().get(stepNo);
                    PayCalc subStepPaid = PayCalc.from(stepPrice, stepNo);
                    subPaid.add(subStepPaid);
                }
                mainPaid.add(subPaid);
            }
        } else {
            PayCalc mainPaid = new PayCalc();
            paidMap.put(mainOrder.getPrimaryOrderId(), mainPaid);
            for (SubPrice subPrice : mainOrder.getAllSubPrice()) {
                PayCalc subPaid = PayCalc.from(subPrice.getOrderPrice());
                paidMap.put(subPrice.getSubOrderId(), subPaid);
                mainPaid.add(subPaid);
            }
        }

        // 计算差额 并记录

        PayCalc zero = new PayCalc();
        PayCalc mainPaid = paidMap.get(mainOrder.getPrimaryOrderId());
        PayCalc mainCancel = CommUtils.getValue(cancelMap, mainOrder.getPrimaryOrderId(), PayCalc::new);
        mainPaid.sub(mainCancel);
        if (zero.anyGt(mainPaid)) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_ILLEGAL);
        }
        fillBack(mainOrder.getOrderPrice(), mainPaid);

        for (SubPrice subPrice : mainOrder.getAllSubPrice()) {
            PayCalc subPaid = paidMap.get(subPrice.getSubOrderId());
            PayCalc subCancel = CommUtils.getValue(cancelMap, subPrice.getSubOrderId(), PayCalc::new);
            subPaid.sub(subCancel);
            if (zero.anyGt(subPaid)) {
                throw new GmallException(OrderErrorCode.ORDER_PRICE_ILLEGAL);
            }
            fillBack(subPrice.getOrderPrice(), subPaid);
        }

        if (mainOrder.getStepOrders() != null) {
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                fillBack(stepOrder.getPrice(), stepOrder.getStepNo(), mainPaid);
            }
        }
    }

    protected void fillBack(OrderPrice target, PayCalc pay) {
        ConfirmPrice confirm = pay.total.toConfirmPrice();
        target.setConfirmPrice(confirm);

        if (pay.stepMap == null || target.getStepAmt() == null) {
            return;
        }
        for (Entry<Integer, PayInnerCalc> en : pay.stepMap.entrySet()) {
            ConfirmPrice stepConfirm = en.getValue().toConfirmPrice();
            StepOrderPrice stepPrice = target.getStepAmt().get(en.getKey());
            stepPrice.setConfirmPrice(stepConfirm);
        }
    }

    protected void fillBack(StepOrderPrice target, int stepNo, PayCalc pay) {
        PayInnerCalc stepPay = pay.stepMap.get(stepNo);
        if (stepPay != null) {   // null: 交易未执行到该阶段就终止的情况
            ConfirmPrice confirm = stepPay.toConfirmPrice();
            target.setConfirmPrice(confirm);
        }
    }

    // 计算分账
    protected void calcSeparateAmt(MainOrder mainOrder) {
        Map<String, LongValue> mainRealMap = new HashMap<>();
        Map<String, LongValue> mainPointMap = new HashMap<>();
        List<SubPrice> allSubPrice = mainOrder.getAllSubPrice();

        for (SubPrice subPrice : allSubPrice) {
            SeparateRule rule = subPrice.getSeparateRule();
            Map<String, Long> subRealMap;
            Map<String, Long> subPointMap;

            // 子订单 X 阶段
            if (MapUtils.isNotEmpty(subPrice.getOrderPrice().getStepAmt())) {
                Map<String, LongValue> subRealMap2 = new HashMap<>();
                Map<String, LongValue> subPointMap2 = new HashMap<>();

                for (Entry<Integer, StepOrderPrice> en : subPrice.getOrderPrice().getStepAmt().entrySet()) {
                    ConfirmPrice confirm = en.getValue().getConfirmPrice();
                    if (confirm == null) {
                        continue;   // 交易未执行到该阶段就终止的情况
                    }
                    Map<String, Long> realMap = calcSeparateAmt(confirm.getConfirmRealAmt(), rule);
                    confirm.setSeparateRealAmt(realMap);
                    mapAdd(realMap, subRealMap2);

                    Map<String, Long> pointMap = calcSeparateAmt(confirm.getConfirmPointAmt(), rule);
                    confirm.setSeparatePointAmt(pointMap);
                    mapAdd(pointMap, subPointMap2);
                }

                subRealMap = toRaw(subRealMap2);
                subPointMap = toRaw(subPointMap2);
            }

            // 子订单
            else {
                ConfirmPrice confirm = subPrice.getOrderPrice().getConfirmPrice();
                subRealMap = calcSeparateAmt(confirm.getConfirmRealAmt(), rule);
                subPointMap = calcSeparateAmt(confirm.getConfirmPointAmt(), rule);
            }

            ConfirmPrice subConfirm = subPrice.getOrderPrice().getConfirmPrice();
            subConfirm.setSeparateRealAmt(subRealMap);
            subConfirm.setSeparatePointAmt(subPointMap);
            mapAdd(subRealMap, mainRealMap);
            mapAdd(subPointMap, mainPointMap);
        }

        ConfirmPrice mainConfirm = mainOrder.getOrderPrice().getConfirmPrice();
        mainConfirm.setSeparateRealAmt(toRaw(mainRealMap));
        mainConfirm.setSeparatePointAmt(toRaw(mainPointMap));

        // 阶段单
        if (CollectionUtils.isNotEmpty(mainOrder.getStepOrders())) {
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                ConfirmPrice stepConfirm = stepOrder.getPrice().getConfirmPrice();
                if (stepConfirm == null) {
                    continue;   // 交易提前终止的情况
                }

                Map<String, LongValue> stepRealMap = new HashMap<>();
                Map<String, LongValue> stepPointMap = new HashMap<>();
                for (SubPrice subPrice : allSubPrice) {
                    StepOrderPrice subStepPrice = subPrice.getOrderPrice().getStepAmt().get(stepOrder.getStepNo());
                    mapAdd(subStepPrice.getConfirmPrice().getSeparatePointAmt(), stepPointMap);
                    mapAdd(subStepPrice.getConfirmPrice().getSeparateRealAmt(), stepRealMap);
                }
                stepConfirm.setSeparatePointAmt(toRaw(stepPointMap));
                stepConfirm.setSeparateRealAmt(toRaw(stepRealMap));
            }
        }
    }

    // 单个最细原子分账计算 (子订单 或 子订单的一个阶段)
    protected Map<String, Long> calcSeparateAmt(Long targetAmt, SeparateRule rule) {
        // 分账规则
        Map<String, Integer> ruleMap = rule == null ? null : rule.getRoles();
        // 分账结果
        Map<String, Long> divideMap = new HashMap<>();

        if (ruleMap == null || ruleMap.isEmpty()) {
            // 全额打给卖家
            divideMap.put(defaultRole, targetAmt);
        } else {
            // 按比例分账
            long totalFact = ruleMap.values().stream().mapToInt(i -> i).sum();
            Divider d = new Divider(targetAmt, totalFact, ruleMap.size());
            List<Entry<String, Integer>> ruleList = sortRoles(ruleMap);
            for (Entry<String, Integer> en : ruleList) {
                int mode = StringUtils.equals(en.getKey(), floorRole)
                        ? BigDecimal.ROUND_FLOOR : BigDecimal.ROUND_HALF_UP;
                Long divideAmt = d.next(en.getValue(), mode);
                divideMap.put(en.getKey(), divideAmt);
            }
        }
        return divideMap;
    }

    // floorRole 靠前
    protected List<Entry<String, Integer>> sortRoles(Map<String, Integer> ruleMap) {
        if (StringUtils.isBlank(floorRole) || !ruleMap.containsKey(floorRole)) {
            return new ArrayList<>(ruleMap.entrySet());
        }
        Map<String, Integer> copy = new HashMap<>();
        copy.putAll(ruleMap);
        Integer value = copy.remove(floorRole);

        List<Entry<String, Integer>> list = new ArrayList<>();
        list.add(new SimpleEntry(floorRole, value));
        list.addAll(copy.entrySet());
        return list;
    }

    // ============= 内部计算模型 =============

    @NoArgsConstructor
    @AllArgsConstructor
    protected static class PayCalc {
        private PayInnerCalc total = new PayInnerCalc();
        private Map<Integer, PayInnerCalc> stepMap = new HashMap<>();

        public static PayCalc from(StepOrderPrice price, int stepNo) {
            PayCalc c = new PayCalc();
            c.total.pointAmt = price.getPointAmt();
            c.total.realAmt = price.getRealAmt();
            c.stepMap.put(stepNo, c.total.copy());
            return c;
        }

        public static PayCalc from(OrderPrice price) {
            PayCalc c = new PayCalc();
            c.total.pointAmt = price.getPointAmt();
            c.total.realAmt = price.getOrderRealAmt();

            if (price.getStepAmt() != null) {
                for (Entry<Integer, StepOrderPrice> en : price.getStepAmt().entrySet()) {
                    PayInnerCalc value = PayInnerCalc.from(en.getValue());
                    c.stepMap.put(en.getKey(), value);
                }
            }
            return c;
        }

        public static PayCalc from(StepRefundFee price) {
            PayCalc c = new PayCalc();
            c.total.pointAmt = getNullZero(price.getCancelPointAmt());
            c.total.realAmt = getNullZero(price.getCancelRealAmt());

            if (price.getStepRefundFee() != null) {
                for (Entry<Integer, RefundFee> en : price.getStepRefundFee().entrySet()) {
                    PayInnerCalc value = PayInnerCalc.from(en.getValue());
                    c.stepMap.put(en.getKey(), value);
                }
            }
            return c;
        }

        public void add(PayCalc other) {
            this.total.pointAmt += other.total.pointAmt;
            this.total.realAmt += other.total.realAmt;
            this.stepMap = CommUtils.merge(this.stepMap, other.stepMap, (v1, v2) -> {
                PayInnerCalc c1 = v1 == null ? new PayInnerCalc() : v1.copy();
                PayInnerCalc c2 = v2 == null ? new PayInnerCalc() : v2;
                c1.add(c2);
                return c1;
            });
        }

        public void sub(PayCalc other) {
            this.total.pointAmt -= other.total.pointAmt;
            this.total.realAmt -= other.total.realAmt;
            this.stepMap = CommUtils.merge(this.stepMap, other.stepMap, (v1, v2) -> {
                PayInnerCalc c1 = v1 == null ? new PayInnerCalc() : v1.copy();
                PayInnerCalc c2 = v2 == null ? new PayInnerCalc() : v2;
                c1.sub(c2);
                return c1;
            });
        }

        public boolean anyGt(PayCalc other) {
            if (total.anyGt(other.total)) {
                return true;
            }

            Map<Integer, PayInnerCalc> m1 = stepMap;
            Map<Integer, PayInnerCalc> m2 = other.stepMap;
            Set<Integer> allKeys = new HashSet<>();
            allKeys.addAll(m1.keySet());
            allKeys.addAll(m2.keySet());
            for (Integer key : allKeys) {
                PayInnerCalc curValue = m1.get(key);
                PayInnerCalc otherValue = m2.get(key);
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

    protected static class PayInnerCalc {
        public long pointAmt;
        public long realAmt;

        public PayInnerCalc copy() {
            PayInnerCalc c = new PayInnerCalc();
            c.pointAmt = pointAmt;
            c.realAmt = realAmt;
            return c;
        }

        public void add(PayInnerCalc other) {
            pointAmt += other.pointAmt;
            realAmt += other.realAmt;
        }

        public void sub(PayInnerCalc other) {
            pointAmt -= other.pointAmt;
            realAmt -= other.realAmt;
        }

        public static PayInnerCalc from(RefundFee fee) {
            PayInnerCalc c = new PayInnerCalc();
            c.pointAmt = fee.getCancelPointAmt();
            c.realAmt = fee.getCancelRealAmt();
            return c;
        }

        public static PayInnerCalc from(StepOrderPrice fee) {
            PayInnerCalc c = new PayInnerCalc();
            c.pointAmt = fee.getPointAmt();
            c.realAmt = fee.getRealAmt();
            return c;
        }

        public boolean anyGt(PayInnerCalc other) {
            return pointAmt > other.pointAmt || realAmt > other.realAmt;
        }

        public ConfirmPrice toConfirmPrice() {
            ConfirmPrice c = new ConfirmPrice();
            c.setConfirmPointAmt(pointAmt);
            c.setConfirmRealAmt(realAmt);
            return c;
        }
    }

    protected static class LongValue {
        private long value;
    }

    protected static void mapAdd(Map<String, Long> value, Map<String, LongValue> target) {
        for (Entry<String, Long> en : value.entrySet()) {
            LongValue t = CommUtils.getValue(target, en.getKey(), LongValue::new);
            t.value += en.getValue();
        }
    }

    protected static Map<String, Long> toRaw(Map<String, LongValue> target) {
        Map<String, Long> map = new HashMap<>();
        for (Entry<String, LongValue> en : target.entrySet()) {
            map.put(en.getKey(), en.getValue().value);
        }
        return map;
    }
}
