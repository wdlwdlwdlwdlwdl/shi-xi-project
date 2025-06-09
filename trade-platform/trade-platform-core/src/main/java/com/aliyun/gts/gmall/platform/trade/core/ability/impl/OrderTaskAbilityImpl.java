package com.aliyun.gts.gmall.platform.trade.core.ability.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.aliyun.gts.gmall.platform.trade.common.constants.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.AutoCancelOrderTask;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.ReversalCancelTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.CancelTaskParam;
import com.aliyun.gts.gmall.platform.trade.core.task.param.ReversalCancelTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSettingQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TimeoutSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j

@Component
public class OrderTaskAbilityImpl implements OrderTaskAbility {

    public static final String ORDER_TASK_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.impl.OrderTaskAbilityImpl";

    @Autowired
    private AutoCancelOrderTask autoCancelOrderTask;

    @Autowired
    private TimeoutSettingRepository timeoutSettingRepository;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private ReversalCancelTask reversalCancelTask;

    /**
     * 订单调度任务创建
     *    用于自动取消订单
     * @param order
     * @param orderStatus
     */
    @Override
    public void orderTask(MainOrder order,Integer orderStatus) {
        //创建自动取消任务
        TimeoutSettingQuery setting = new TimeoutSettingQuery();
        setting.setOrderStatus(orderStatus);
        CancelTaskParam param = new CancelTaskParam();
        if(order.getOrderAttr() != null){
            Integer payType = Integer.valueOf(order.getOrderAttr().getPayChannel());
            if(PayTypeEnum.INSTALLMENT.getCode().equals(payType) ||
                PayTypeEnum.LOAN.getCode().equals(payType)){
                payType = PayTypeEnum.LOAN_INSTALLMENT.getCode();
            }
            setting.setPayType(payType);
        }
        TcTimeoutSettingDO tcTimeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(setting);
        if( tcTimeoutSettingDO == null ){
            setting.setPayType(null);
            tcTimeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(setting);
            if(tcTimeoutSettingDO != null){
                extracted(order, orderStatus, param, tcTimeoutSettingDO);
            }
        } else {
            extracted(order, orderStatus, param, tcTimeoutSettingDO);
        }
    }

    private void extracted(MainOrder order, Integer orderStatus, CancelTaskParam param, TcTimeoutSettingDO tcTimeoutSettingDO) {
        param.setPayType(Integer.valueOf(order.getOrderAttr().getPayChannel()));
        param.setOrderStatus(orderStatus);
        param.setPrimaryOrderId(order.getPrimaryOrderId());
        param.setSellerId(order.getSeller().getSellerId());
        param.setTimeType(tcTimeoutSettingDO.getTimeType());
        param.setTimeRule(tcTimeoutSettingDO.getTimeRule());
        ScheduleTask task = autoCancelOrderTask.doBuildTask(param);
        orderTaskService.createScheduledTask(Lists.newArrayList(task));
    }

    /**
     * 退单调度任务创建
     *    用于自动取消退单
     * @param reversal
     * @param orderStatus
     */
    public void reversalTask(MainReversal reversal, Integer orderStatus) {
        TimeoutSettingQuery setting = new TimeoutSettingQuery();
        setting.setOrderStatus(orderStatus);
        ReversalCancelTaskParam param = new ReversalCancelTaskParam();
        if(reversal.getMainOrder().getOrderAttr() != null){
            Integer payType = Integer.valueOf(reversal.getMainOrder().getOrderAttr().getPayChannel());
            if(PayTypeEnum.INSTALLMENT.getCode().equals(payType) ||
                PayTypeEnum.LOAN.getCode().equals(payType)){
                payType = PayTypeEnum.LOAN_INSTALLMENT.getCode();
            }
            setting.setPayType(payType);
        }
        TcTimeoutSettingDO tcTimeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(setting);
        if( tcTimeoutSettingDO == null ){
            setting.setPayType(null);
            tcTimeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(setting);
        } else {
            param.setPayType(Integer.valueOf(reversal.getMainOrder().getOrderAttr().getPayChannel()));
            param.setPrimaryReversalId(reversal.getPrimaryReversalId());
            param.setOrderStatus(orderStatus);
            param.setPrimaryOrderId(reversal.getMainOrder().getPrimaryOrderId());
            param.setSellerId(reversal.getMainOrder().getSeller().getSellerId());
            param.setTimeType(tcTimeoutSettingDO.getTimeType());
            param.setTimeRule(tcTimeoutSettingDO.getTimeRule());
            ScheduleTask task = reversalCancelTask.doBuildTask(param);
            orderTaskService.createScheduledTask(Lists.newArrayList(task));
        }
    }
}
