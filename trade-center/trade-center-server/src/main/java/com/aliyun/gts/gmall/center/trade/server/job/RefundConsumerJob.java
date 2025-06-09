package com.aliyun.gts.gmall.center.trade.server.job;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.JavaProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayRefundDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 退款执行补偿任务
 */
@Component
@Slf4j
public class RefundConsumerJob extends JavaProcessor {

    @Autowired
    private PayRefundDomainService payRefundDomainService;

    @Override
    public ProcessResult process(JobContext jobContext) throws Exception {

        String params = jobContext.getJobParameters();
        RefundSuccessMessage message = JSONObject.parseObject(params, RefundSuccessMessage.class);
        try {
            checkParam(message);
            payRefundDomainService.payRefundExecute(message);
        } catch (Exception e) {
            log.error("PayRefundConsumer.process occurred exceptions! reversalId = {} ",
                    message.getPrimaryReversalId(), e);
            return new ProcessResult(false);
        }

        return new ProcessResult(true);
    }

    private void checkParam(RefundSuccessMessage message) {
        if (message == null || message.getRefundId() == null
                || message.getCustId() == null
                || message.getPrimaryOrderId() == null
                || message.getPrimaryReversalId() == null) {
            throw new RuntimeException("arguments missing");
        }
    }

}
