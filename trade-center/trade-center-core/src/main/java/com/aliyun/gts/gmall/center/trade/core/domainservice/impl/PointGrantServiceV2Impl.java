package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.core.domainservice.ReversalAdjustService;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.PointRollbackExtParam;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PointGrantRepository;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 不退成负积分, 积分不足时从退款金额中扣
 */
@Service
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "pointGrantService",
        havingValue = "v2")
public class PointGrantServiceV2Impl extends PointGrantServiceImpl {
    private static final ThreadLocal<Boolean> needRefund = new ThreadLocal<>();

    @Autowired
    private ReversalAdjustService reversalAdjustService;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;
    @Autowired
    private PointGrantRepository pointGrantRepository;

    @Override
    public void rollbackOnReversalSuccess(Long primaryReversalId) {
        // 不处理
    }

    @Override
    public boolean rollbackBeforeRefund(Long primaryReversalId) {
        needRefund.set(true);
        super.rollbackPointGrant(primaryReversalId);
        return needRefund.get();
    }

    @Override
    protected void callRollbackPointGrant(PointRollbackParam param, MainReversal reversal) {
        PointExchange ex = reversal.getMainOrder().orderAttr().getPointExchange();
        if (ex == null) {
            ex = pointExchangeRepository.getExchangeRate();
        }

        long toRollbackCount = param.getCount();
        // 用于至少需要有的积分数, 如小于该值, 退款金额不足以覆盖应退回的积分, 则退0元, 不扣用户积分
        long minPointCount = toRollbackCount - ex.exAmtToCount(reversal.getCancelAmt()) + 1000;
        if (minPointCount < 0) {
            minPointCount = 0;
        }

        PointRollbackExtParam ext = new PointRollbackExtParam(param);
        ext.setMinChangeCount(minPointCount);
        long rbCount = pointGrantRepository.rollbackGrantPointPositive(ext);
        if (rbCount >= toRollbackCount) {
            return;     // 积分全部退回了
        }

        // 积分不足, 用退款金额抵
        long reduceCount = toRollbackCount - rbCount;
        long reduceAmt = ex.exCountToAmt(reduceCount);
        reduceAmt = Math.min(reduceAmt, reversal.getCancelAmt());
        reduceCount = ex.exAmtToCount(reduceAmt);
        String reason = String.format(I18NMessageUtils.getMessage("refund.points")+" %s "+I18NMessageUtils.getMessage("points.deducted")+", 由于积分不足从退款金额中扣除",  //# "该退款应退回订单赠送的积分 %s 个
                displayValue(toRollbackCount));
        reversal.getReversalFeatures().putExtend(KEY_RETURN, String.valueOf(rbCount));
        reversalAdjustService.reduceFee(reversal, reduceCount, reason);
        if (reversal.getCancelAmt() == 0L) {
            needRefund.set(false);
        }
    }

    private String displayValue(long pointCount) {
        return BigDecimal.valueOf(pointCount)
                .divide(BigDecimal.valueOf(1000))
                .toString();
    }
}
