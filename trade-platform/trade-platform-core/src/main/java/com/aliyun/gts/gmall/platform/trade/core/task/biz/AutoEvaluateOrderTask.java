package com.aliyun.gts.gmall.platform.trade.core.task.biz;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.EvaluationService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

@Slf4j
@Component
public class AutoEvaluateOrderTask  extends AbstractScheduledTask {

    @Autowired
    private OrderConfigService orderConfigService;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    EvaluationService evaluationService;

    @Override
    protected ScheduleTask doBuildTask(TaskParam param) {
        SellerTradeConfig cfg = orderConfigService.getSellerConfig(param.getSellerId());
        Date scheduleTime = new Date(System.currentTimeMillis() + 1000L * cfg.getAutoEvaluateTimeInSec());

        ScheduleTask t = new ScheduleTask();
        t.setScheduleTime(scheduleTime);
        t.setType(getTaskType());
        t.setPrimaryOrderId(param.getPrimaryOrderId());
        // 如果有自定义参数在这里添加
        // t.setParams(xxx);
        return t;
    }

    @Override
    protected ExecuteResult doExecute(ScheduleTask task) {
        Long primaryOrderId = task.getPrimaryOrderId();
        evaluationService.autoEvaluation(primaryOrderId);
        return ExecuteResult.success("ok");
    }
}
