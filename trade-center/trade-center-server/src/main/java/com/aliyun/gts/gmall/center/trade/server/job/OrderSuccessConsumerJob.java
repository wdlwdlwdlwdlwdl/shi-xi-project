package com.aliyun.gts.gmall.center.trade.server.job;

import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.JavaProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PaySplitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderSuccessConsumerJob extends JavaProcessor {

    @Autowired
    private PaySplitService paySplitService;

    @Override
    public ProcessResult process(JobContext jobContext) {
        String primaryOrderId = jobContext.getJobParameters();

        try {
            paySplitService.paySplitAfterTradeSuccess(Long.valueOf(primaryOrderId));
        } catch (Exception e) {
            log.error("com.aliyun.gts.gmall.center.trade.server.job.OrderSuccessConsumerJob.process! primaryOrderId = {} ",
                    primaryOrderId, e);
            return new ProcessResult(false);
        }
        return new ProcessResult(true);
    }
}
