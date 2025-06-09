package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.constants.ReversalAdjConstants;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ReversalAdjustService;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalFeeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalMessageAbility;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@Slf4j
@Service
public class ReversalAdjustServiceImpl implements ReversalAdjustService {

    @Autowired
    private TransactionProxy transactionProxy;
    @Autowired
    private ReversalMessageAbility reversalMessageAbility;
    @Autowired
    private TcReversalRepository tcReversalRepository;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;
    @Autowired
    private ReversalFeeAbility reversalFeeAbility;

    @Override
    public void reduceFee(MainReversal reversal, long pointCount, String reason) {
        PointExchange ex = reversal.getMainOrder().getOrderAttr().getPointExchange();
        if (ex == null) { // 非积分抵扣的订单不记录该值
            ex = pointExchangeRepository.getExchangeRate();
        }

        ReduceFee reduce = new ReduceFee();
        // 先从现金中扣, 防止现金退完剩下纯积分支付的不会处理赠积分
        long toReduceAmt = ex.exCountToAmt(pointCount);
        {
            long cancelRealAmt = NumUtils.getNullZero(reversal.getReversalFeatures().getCancelRealAmt());
            reduce.reduceRealAmt = Math.min(cancelRealAmt, toReduceAmt);
            toReduceAmt -= reduce.reduceRealAmt;
        }
        // 再从积分支付中扣
        if (toReduceAmt > 0) {
            long toReduceCount = ex.exAmtToCount(toReduceAmt);
            reduce.reducePointCount = toReduceCount;
        }
        calcReduce(reversal, reduce, ex);


        reversal.getReversalFeatures().putExtend(ReversalAdjConstants.IS_ADJ, "1");
        reversal.getReversalFeatures().putExtend(ReversalAdjConstants.ADJ_REASON, reason);
        transactionProxy.callTx(() -> {
            saveReduceResult(reversal);
        });
    }

    private void calcReduce(MainReversal reversal, ReduceFee reduce, PointExchange ex) {
        ReduceFee remain = reduce.copy();
        Multimap<Integer, RefundFee> subStepMap = ArrayListMultimap.create();
        List<RefundFee> subFeeList = new ArrayList<>();

        for (SubReversal sub : reversal.getSubReversals()) {
            ReversalFeatureDO subFee = sub.reversalFeatures();
            subFeeList.add(subFee);

            // 多阶段
            if (MapUtils.isNotEmpty(subFee.getStepRefundFee())) {
                for (Entry<Integer, RefundFee> step : subFee.getStepRefundFee().entrySet()) {
                    remain = calcReduce(step.getValue(), remain, ex);
                    subStepMap.put(step.getKey(), step.getValue());
                }
                calcSum(subFee, subFee.getStepRefundFee().values());
            }

            // 非多阶段
            else {
                remain = calcReduce(subFee, remain, ex);
            }
            sub.setCancelAmt(subFee.getCancelTotalAmt());
        }

        // 主售后单
        calcSum(reversal.reversalFeatures(), subFeeList);
        reversal.setCancelAmt(reversal.reversalFeatures().getCancelTotalAmt());
        if (!subStepMap.isEmpty()) {
            // 多阶段
            for (Integer stepNo : subStepMap.keySet()) {
                Collection<RefundFee> subFees = subStepMap.get(stepNo);
                RefundFee mainFee = reversal.getReversalFeatures().getStepRefundFee().get(stepNo);
                if (mainFee == null) {
                    throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG,
                            String.format("reversal error, primaryReversalId: %s , stepNo: %s ",
                                    reversal.getPrimaryReversalId(), stepNo));
                }
                calcSum(mainFee, subFees);
            }
        }
    }

    // return remain
    private ReduceFee calcReduce(RefundFee fee, ReduceFee reduce, PointExchange ex) {
        long cancelReamAmt = NumUtils.getNullZero(fee.getCancelRealAmt());
        long reduceRealAmt = Math.min(reduce.reduceRealAmt, cancelReamAmt);
        if (reduceRealAmt > 0) {
            fee.setCancelRealAmt(cancelReamAmt - reduceRealAmt);
            reduceSeparateMap(fee.getCancelRealAmt(), fee.getCancelSeparateRealAmt());
        }

        long cancelPointCount = NumUtils.getNullZero(fee.getCancelPointCount());
        long reducePointCount = Math.min(reduce.reducePointCount, cancelPointCount);
        if (reducePointCount > 0) {
            fee.setCancelPointCount(cancelPointCount - reducePointCount);
            fee.setCancelPointAmt(ex.exCountToAmt(fee.getCancelPointCount()));
            reduceSeparateMap(fee.getCancelPointAmt(), fee.getCancelSeparatePointAmt());
        }
        ReduceFee remain = reduce.copy();
        remain.reduceRealAmt -= reduceRealAmt;
        remain.reducePointCount -= reducePointCount;
        return remain;
    }

    private void calcSum(RefundFee target, Collection<RefundFee> sources) {
        long cancelPointAmt = 0L;
        long cancelPointCount = 0L;
        long cancelRealAmt = 0L;
        Map<String, Long> cancelSeparateRealAmt = null;
        Map<String, Long> cancelSeparatePointAmt = null;
        for (RefundFee s : sources) {
            cancelPointAmt += NumUtils.getNullZero(s.getCancelPointAmt());
            cancelPointCount += NumUtils.getNullZero(s.getCancelPointCount());
            cancelRealAmt += NumUtils.getNullZero(s.getCancelRealAmt());
            cancelSeparateRealAmt = CommUtils.addMergeLong(cancelSeparateRealAmt, s.getCancelSeparateRealAmt());
            cancelSeparatePointAmt = CommUtils.addMergeLong(cancelSeparatePointAmt, s.getCancelSeparatePointAmt());
        }
        target.setCancelPointAmt(cancelPointAmt);
        target.setCancelPointCount(cancelPointCount);
        target.setCancelRealAmt(cancelRealAmt);
        target.setCancelSeparateRealAmt(cancelSeparateRealAmt);
        target.setCancelSeparatePointAmt(cancelSeparatePointAmt);
    }

    private void reduceSeparateMap(long total, Map<String, Long> separateMap) {
        if (MapUtils.isEmpty(separateMap)) {
            return;
        }
        long sum = separateMap.values().stream().mapToLong(Long::valueOf).sum();
        Divider div = new Divider(total, sum, separateMap.size());
        Map<String, Long> newMap = new HashMap<>();
        for (Entry<String, Long> en : separateMap.entrySet()) {
            long newValue = div.next(en.getValue());
            newMap.put(en.getKey(), newValue);
        }
        separateMap.putAll(newMap);
    }

    private static class ReduceFee {
        long reducePointCount;
        long reduceRealAmt;

        ReduceFee copy() {
            ReduceFee c = new ReduceFee();
            c.reducePointCount = reducePointCount;
            c.reduceRealAmt = reduceRealAmt;
            return c;
        }
    }

    private void saveReduceResult(MainReversal reversal) {
        TcReversalDO mainUp = new TcReversalDO();
        mainUp.setReversalId(reversal.getPrimaryReversalId());
        mainUp.setReversalFeatures(reversal.getReversalFeatures());
        mainUp.setCancelAmt(reversal.getCancelAmt());
        mainUp.setVersion(reversal.getVersion());
        boolean updated = tcReversalRepository.updateByReversalIdVersion(mainUp);
        if (!updated) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }

        for (SubReversal sub : reversal.getSubReversals()) {
            TcReversalDO subUp = new TcReversalDO();
            subUp.setReversalId(sub.getReversalId());
            subUp.setReversalFeatures(sub.getReversalFeatures());
            subUp.setCancelAmt(sub.getCancelAmt());
            subUp.setVersion(sub.getVersion());
            updated = tcReversalRepository.updateByReversalIdVersion(subUp);
            if (!updated) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }
}
