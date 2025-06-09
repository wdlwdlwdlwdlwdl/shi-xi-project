package com.aliyun.gts.gmall.center.trade.core.extension.common;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.task.defaultimpl.DefaultScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.CloseUnpaidOrderTask;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.UnlockInventoryTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 对公支付 账期支付  无自动关单和回补库存
 */
@Slf4j
public class CommonScheduleTaskExt extends DefaultScheduleTaskExt {

    @Autowired
    CloseUnpaidOrderTask closeUnpaidOrderTask;
    @Autowired
    UnlockInventoryTask unlockInventoryTask;
    @Autowired
    PayQueryService payQueryService;

    @Override
    public ScheduleTask beforeCreateTask(ScheduleTask task) {
        if (closeUnpaidOrderTask.getTaskType().equals(task.getType())
                || unlockInventoryTask.getTaskType().equals(task.getType())) {
            if (task.getMainOrder() != null) {
                OrderPay orderPay = payQueryService.queryByOrder(task.getMainOrder());
                String payChannel = orderPay.getPayChannel();
                if (PayChannelEnum.CAT.getCode().equals(payChannel)
                        || PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(payChannel)) {
                    return null;
                }
            }
        }
        return super.beforeCreateTask(task);
    }
}
