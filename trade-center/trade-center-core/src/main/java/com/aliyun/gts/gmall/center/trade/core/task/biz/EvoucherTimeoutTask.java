package com.aliyun.gts.gmall.center.trade.core.task.biz;

import com.aliyun.gts.gmall.center.trade.common.constants.EvoucherStatusEnum;
import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSysProcessService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class EvoucherTimeoutTask extends AbstractScheduledTask<ScheduleTimeTaskParam> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private OrderSysProcessService orderSysProcessService;

    static {
        TaskRegister.addTaskClass(EvoucherTimeoutTask.class);
    }

    @Override
    protected ScheduleTask doBuildTask(ScheduleTimeTaskParam param) {
        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(param.getScheduleTime());
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask scheduleTask) throws Exception {
        long primaryOrderId = scheduleTask.getPrimaryOrderId();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId,
                OrderQueryOption.builder().build());

        // 非待核销
        if (OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()) != OrderStatusEnum.ORDER_SENDED) {
            return ExecuteResult.success("no need");
        }

        // 退款
        SystemRefund sysRefund = new SystemRefund();
        sysRefund.setReversalType(ReversalTypeEnum.REFUND_ITEM.getCode());
        sysRefund.setMainOrder(mainOrder);
        sysRefund.setAfterSetAmt(this::fixCancelAmt);
        orderSysProcessService.systemRefund(sysRefund);
        return ExecuteResult.success("ok");
    }

    // 这里已填充退款金额数量为全退，需要按比例减去已核销的券
    private void fixCancelAmt(MainReversal reversal) {
        long sumCancelRealAmt = 0L;
        long sumCancelPointAmt = 0L;
        long sumCancelPointCount = 0L;
        int sumCancelQty = 0;
        for (SubReversal sub : reversal.getSubReversals()) {
            long orderId = sub.getSubOrder().getOrderId();
            List<TcEvoucherDO> evList = tcEvoucherRepository.queryByOrderId(orderId);

            int toRefund = 0;
            int notRefund = 0;

            for (TcEvoucherDO ev : evList) {
                EvoucherStatusEnum status = EvoucherStatusEnum.codeOf(ev.getStatus());
                if (status == EvoucherStatusEnum.NOT_USED) {
                    toRefund++;
                } else if (status == EvoucherStatusEnum.WRITE_OFF) {
                    notRefund++;
                }
            }

            if (notRefund == 0) {   // 全退, 无需扣除
                sumCancelRealAmt += sub.reversalFeatures().getCancelRealAmt();
                sumCancelPointAmt += sub.reversalFeatures().getCancelPointAmt();
                sumCancelPointCount += sub.reversalFeatures().getCancelPointCount();
                sumCancelQty += sub.getCancelQty();
                continue;
            }
            if (toRefund == 0) {    // 理论上不会出现
                throw new GmallException(CommonErrorCode.SERVER_ERROR);
            }

            // 按比例扣除
            long realAmt = divide(sub.reversalFeatures().getCancelRealAmt(), toRefund, notRefund);
            long pointAmt = divide(sub.reversalFeatures().getCancelPointAmt(), toRefund, notRefund);
            long pointCount = divide(sub.reversalFeatures().getCancelPointCount(), toRefund, notRefund);

            sub.reversalFeatures().setCancelRealAmt(realAmt);
            sub.reversalFeatures().setCancelPointAmt(pointAmt);
            sub.reversalFeatures().setCancelPointCount(pointCount);
            sub.setCancelAmt(realAmt + pointAmt);
            sub.setCancelQty(toRefund);

            sumCancelRealAmt += realAmt;
            sumCancelPointAmt += pointAmt;
            sumCancelPointCount += pointCount;
            sumCancelQty += toRefund;
        }
        reversal.reversalFeatures().setCancelRealAmt(sumCancelRealAmt);
        reversal.reversalFeatures().setCancelPointAmt(sumCancelPointAmt);
        reversal.reversalFeatures().setCancelPointCount(sumCancelPointCount);
        reversal.setCancelAmt(sumCancelRealAmt + sumCancelPointAmt);
        reversal.setCancelQty(sumCancelQty);
    }

    private static long divide(Long total, int toRefund, int notRefund) {
        List<Long> divide = DivideUtils.divide(NumUtils.getNullZero(total),
                Arrays.asList((long) toRefund, (long) notRefund));
        return divide.get(0);
    }
}
