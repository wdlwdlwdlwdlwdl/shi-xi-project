package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.PresaleConstants;
import com.aliyun.gts.gmall.center.trade.common.util.DateUtils;
import com.aliyun.gts.gmall.center.trade.core.task.biz.DepositTailFeeTimeoutTask;
import com.aliyun.gts.gmall.center.trade.core.task.param.ScheduleTimeTaskParam;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.OrderChangedNotifyServiceImpl;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 预售扩展
 */
@Slf4j
@Extension(points = {StepOrderProcessExt.class})
public class PresaleStepOrderProcessExt extends DefaultStepOrderProcessExt {

    @Autowired
    private DepositTailFeeTimeoutTask depositTailFeeTimeoutTask;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;
    @Autowired
    private TransactionProxy transactionProxy;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Override
    protected void afterPaySuccessTx(MainOrder mainOrder, OrderPay orderPay) {
        super.afterPaySuccessTx(mainOrder, orderPay);
        if (orderPay.getStepNo() != null && orderPay.getStepNo().intValue() == 1) {
            createTimeoutTask(mainOrder);
        }
    }

    /**
     * epay 预售改为
     * @param mainOrder
     * @param orderPay
     */
    public void onPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        List<OrderChangeNotify> messageList = new ArrayList();
        this.transactionProxy.callTx(() -> {
            MainOrder main = mainOrder;
            if (OrderStatusEnum.codeOf(main.getPrimaryOrderStatus()) == OrderStatusEnum.WAITING_FOR_PAYMENT) {
                main = updateOrderPayStatus(main, messageList);
            }

            main.getCurrentStepOrder().features().setPayTime(System.currentTimeMillis());
            this.saveNextStep(main, messageList, OrderChangeOperateEnum.STEP_CUST_PAY);
            this.afterPaySuccessTx(mainOrder, orderPay);
        });
        for (OrderChangeNotify message : messageList) {
            orderChangedNotifyService.afterStatusChange(message);
            break;
        }

    }

    public MainOrder updateOrderPayStatus(MainOrder mainOrder, List<OrderChangeNotify> messageResult) {
        List<TcOrderOperateFlowDO> list = Lists.newArrayList(new TcOrderOperateFlowDO[]{OrderChangedNotifyServiceImpl.buildOrderPay(mainOrder, OrderStatusEnum.WAITING_FOR_PAYMENT, OrderStatusEnum.PARTIALLY_PAID)});
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        this.tcOrderRepository.updateStatusAndStageByPrimaryId(primaryOrderId, OrderStatusEnum.PARTIALLY_PAID.getCode(), OrderStageEnum.ON_SALE.getCode(), (Integer)null);
        mainOrder = this.orderQueryAbility.getMainOrder(primaryOrderId);
        messageResult.add(OrderChangeNotify.builder().mainOrder(mainOrder).flows(list).op(OrderChangeOperateEnum.CUST_PAY).build());
        return mainOrder;
    }

    // 创建尾款支付超时任务
    private void createTimeoutTask(MainOrder mainOrder) {
        String tailEnd = mainOrder.orderAttr().stepContextProps().get(PresaleConstants.CONTEXT_TAIL_END);
        if (StringUtils.isBlank(tailEnd)) {
            return;
        }

        Date endDate = DateUtils.parseDateTime(tailEnd);
        ScheduleTimeTaskParam param = new ScheduleTimeTaskParam();
        param.setScheduleTime(endDate);
        param.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        param.setSellerId(mainOrder.getSeller().getSellerId());
        param.setBizCodes(BizCodeEntity.getOrderBizCode(mainOrder));
        ScheduleTask scheduleTask = depositTailFeeTimeoutTask.buildTask(param);
        orderTaskService.createScheduledTask(Arrays.asList(scheduleTask));
    }
}
