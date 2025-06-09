package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalModifyRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.TimeoutSettingService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.ReversalCancelTaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ReceiverRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.ReceiverRpcConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * 售后待卖家同意超时，自动同意
 */
@Component
@Slf4j
public class ReversalCancelTask extends AbstractScheduledTask<ReversalCancelTaskParam> {

    @Autowired
    private ReversalProcessAbility reversalProcessAbility;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Autowired
    private ReceiverRpcConverter receiverRpcConverter;

    @Autowired
    private TimeoutSettingService timeoutSettingService;

    @Override
    public ScheduleTask doBuildTask(ReversalCancelTaskParam param) {
        Date scheduleTime ;
        if(param.getTimeType() == 0){
            //分
            scheduleTime = new Date(System.currentTimeMillis() + 60*1000L * Long.parseLong(param.getTimeRule()));
        }else if(param.getTimeType() == 1){
            //时
            scheduleTime = new Date(System.currentTimeMillis() + 60*60*1000L * Long.parseLong(param.getTimeRule()));
        }else{
            //天
            scheduleTime = new Date(System.currentTimeMillis() + 60*60*24*1000L * Long.parseLong(param.getTimeRule()));
        }
        ScheduleTask task = new ScheduleTask();
        task.setScheduleTime(scheduleTime);
        task.setType(getTaskType());
        task.setPrimaryOrderId(param.getPrimaryOrderId());
        task.putParam("primaryReversalId", param.getPrimaryReversalId());
        task.putParam("orderStatus", param.getOrderStatus());
        task.putParam("payType", param.getPayType());
        task.putParam("primaryOrderId", param.getPrimaryOrderId());
        task.putParam("timeType", param.getTimeType());
        task.putParam("timeRule", param.getTimeRule());
        return task;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        Map<String, Object> params = task.getParams();
        Integer orderStatus = Integer.valueOf(params.get("orderStatus").toString());
        long primaryReversalId = task.getParamLong("primaryReversalId");
        TcReversalDO reversal = tcReversalRepository.queryByReversalId(primaryReversalId);
        if (reversal == null) {
            return ExecuteResult.success("reversal not exist");
        }
        if (!orderStatus.equals(reversal.getReversalStatus())) {
            return ExecuteResult.success("no need");
        }
        ReversalModifyRpcReq req = new ReversalModifyRpcReq();
        req.setPrimaryReversalId(primaryReversalId);
        req.setSystemOperate(true);
        req.setReversalStatus(orderStatus);
        reversalProcessAbility.cancelBySystem(req);
        return ExecuteResult.success("ok");
    }
}
