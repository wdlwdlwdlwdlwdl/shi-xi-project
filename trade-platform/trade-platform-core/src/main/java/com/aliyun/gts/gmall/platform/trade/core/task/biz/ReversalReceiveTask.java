package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalModifyRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.ReversalTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 售后待卖家收货超时，自动确认收货
 */
@Component
@Slf4j
public class ReversalReceiveTask extends AbstractScheduledTask<ReversalTaskParam> {

    @Autowired
    private ReversalProcessAbility reversalProcessAbility;
    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Override
    protected ScheduleTask doBuildTask(ReversalTaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * cfg.getAutoReceiveReversalTimeInSec());

        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(scheduleTime);
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        t.putParam("primaryReversalId", param.getPrimaryReversalId());
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        long primaryReversalId = task.getParamLong("primaryReversalId");
        TcReversalDO reversal = tcReversalRepository.queryByReversalId(primaryReversalId);
        if (reversal == null) {
            return ExecuteResult.success("reversal not exist");
        }
        if (reversal.getReversalStatus().intValue() != ReversalStatusEnum.WAIT_CONFIRM_RECEIVE.getCode()) {
            return ExecuteResult.success("reversal not WAIT_CONFIRM_RECEIVE");
        }

        ReversalModifyRpcReq req = new ReversalModifyRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setSellerId(reversal.getSellerId());
        req.setSystemOperate(true);
        reversalProcessAbility.sellerConfirmDeliver(req);
        return ExecuteResult.success("ok");
    }
}
